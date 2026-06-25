package br.com.guilherme.projetoBase.Controller;

import br.com.guilherme.projetoBase.DTO.AtualizarStatusPedidoRequest;
import br.com.guilherme.projetoBase.DTO.CriarPedidoRequest;
import br.com.guilherme.projetoBase.DTO.ErroResponse;
import br.com.guilherme.projetoBase.DTO.PedidoDetalheResponse;
import br.com.guilherme.projetoBase.DTO.PedidoListResponse;
import br.com.guilherme.projetoBase.DTO.PedidoResponse;
import br.com.guilherme.projetoBase.Model.CanalPedido;
import br.com.guilherme.projetoBase.Security.JwtService;
import br.com.guilherme.projetoBase.Service.PedidoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pedidos")
public class PedidoController {

    private final PedidoService pedidoService;
    private final JwtService    jwtService;

    public PedidoController(PedidoService pedidoService,
                            JwtService jwtService) {
        this.pedidoService = pedidoService;
        this.jwtService    = jwtService;
    }

    @Operation(summary = "Lista todos os pedidos, com filtro opcional por canal")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Canal inválido"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @GetMapping("/listar")
    public ResponseEntity<List<PedidoListResponse>> listar(
            @RequestParam(required = false) CanalPedido canalPedido) {
        return ResponseEntity.ok(pedidoService.listarTodos(canalPedido));
    }

    @Operation(summary = "Busca um pedido pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDetalheResponse> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @Operation(summary = "Atualiza o status de um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Status atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Status inválido ou pedido em estado terminal"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @PatchMapping("/{id}/status")
    public ResponseEntity<PedidoDetalheResponse> atualizarStatus(
            @PathVariable UUID id,
            @RequestBody @Valid AtualizarStatusPedidoRequest request) {
        return ResponseEntity.ok(pedidoService.atualizarStatus(id, request));
    }

    @Operation(summary = "Cria um novo pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido efetuado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou estoque insuficiente"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "404", description = "Cliente, produto ou unidade não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PostMapping("/criar")
    public ResponseEntity<PedidoResponse> criar(
            @RequestBody @Valid CriarPedidoRequest request,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtService.extractEmail(token);

        PedidoResponse response = pedidoService.criar(request, email);

        return ResponseEntity.status(201).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> handleValidation(MethodArgumentNotValidException ex) {
        String mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return ResponseEntity.status(400)
                .body(new ErroResponse(400, mensagem, LocalDateTime.now()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErroResponse> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(400)
                .body(new ErroResponse(400, ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErroResponse> handleForbidden(AccessDeniedException ex) {
        return ResponseEntity.status(403)
                .body(new ErroResponse(403, "Acesso negado", LocalDateTime.now()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErroResponse> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity.status(404)
                .body(new ErroResponse(404, ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> handleUnreadable(HttpMessageNotReadableException ex) {
        String cause = ex.getMostSpecificCause().getMessage();
        if (cause != null && cause.contains("UUID")) {
            return ResponseEntity.status(404)
                    .body(new ErroResponse(404, "Pedido não encontrado", LocalDateTime.now()));
        }
        return ResponseEntity.status(400)
                .body(new ErroResponse(400, "Requisição inválida: " + cause, LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (UUID.class.equals(ex.getRequiredType())) {
            return ResponseEntity.status(404)
                    .body(new ErroResponse(404, "Pedido não encontrado", LocalDateTime.now()));
        }
        String mensagem = "Valor inválido para o parâmetro '" + ex.getName() + "': " + ex.getValue();
        return ResponseEntity.status(400)
                .body(new ErroResponse(400, mensagem, LocalDateTime.now()));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErroResponse> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(500)
                .body(new ErroResponse(500, ex.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> handleGeneric(Exception ex) {
        return ResponseEntity.status(500)
                .body(new ErroResponse(500, "Erro interno do servidor", LocalDateTime.now()));
    }
}
