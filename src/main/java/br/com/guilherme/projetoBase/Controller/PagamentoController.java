package br.com.guilherme.projetoBase.Controller;

import br.com.guilherme.projetoBase.DTO.ErroResponse;
import br.com.guilherme.projetoBase.DTO.PagamentoRequest;
import br.com.guilherme.projetoBase.DTO.PagamentoResponse;
import br.com.guilherme.projetoBase.Service.PagamentoService;
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
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pagamentos")
public class PagamentoController {

    private final PagamentoService pagamentoService;

    public PagamentoController(PagamentoService pagamentoService) {
        this.pagamentoService = pagamentoService;
    }

    @Operation(summary = "Processa o pagamento de um pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pagamento processado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos ou pedido não elegível para pagamento"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Pedido não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('CLIENTE', 'ATENDENTE')")
    @PostMapping
    public ResponseEntity<PagamentoResponse> processar(
            @RequestBody @Valid PagamentoRequest request) {
        return ResponseEntity.status(201).body(pagamentoService.processar(request));
    }

    @Operation(summary = "Consulta o pagamento de um pedido pelo ID do pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pagamento encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Pagamento não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @GetMapping("/{pedidoId}")
    public ResponseEntity<PagamentoResponse> consultar(
            @PathVariable UUID pedidoId) {
        return ResponseEntity.ok(pagamentoService.consultar(pedidoId));
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
