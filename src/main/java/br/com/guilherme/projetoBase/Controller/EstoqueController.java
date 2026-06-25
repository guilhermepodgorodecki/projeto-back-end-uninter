package br.com.guilherme.projetoBase.Controller;

import br.com.guilherme.projetoBase.DTO.ErroResponse;
import br.com.guilherme.projetoBase.DTO.EstoqueDetalheResponse;
import br.com.guilherme.projetoBase.DTO.EstoqueListResponse;
import br.com.guilherme.projetoBase.DTO.EstoqueRequest;
import br.com.guilherme.projetoBase.DTO.EstoqueResponse;
import br.com.guilherme.projetoBase.DTO.ReporEstoqueRequest;
import br.com.guilherme.projetoBase.Service.EstoqueService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/estoque")
@RequiredArgsConstructor
public class EstoqueController {

    private final EstoqueService estoqueService;

    public EstoqueController(EstoqueService estoqueService) {
        this.estoqueService = estoqueService;
    }

    @Operation(summary = "Lista estoque com filtros opcionais por produto e/ou unidade")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @GetMapping("/listar")
    public ResponseEntity<List<EstoqueListResponse>> listar(
            @RequestParam(required = false) Integer produtoId,
            @RequestParam(required = false) Integer unidadeId) {
        return ResponseEntity.ok(estoqueService.listarEstoque(produtoId, unidadeId));
    }

    @Operation(summary = "Busca um registro de estoque pelo ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque encontrado"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE', 'ATENDENTE')")
    @GetMapping("/{id}")
    public ResponseEntity<EstoqueDetalheResponse> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(estoqueService.buscarPorId(id));
    }

    @Operation(summary = "Repõe quantidade no estoque de um produto")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estoque reposto com sucesso"),
            @ApiResponse(responseCode = "400", description = "Quantidade inválida"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Estoque não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @PatchMapping("/{produtoId}/repor")
    public ResponseEntity<EstoqueDetalheResponse> repor(
            @PathVariable Integer produtoId,
            @RequestBody @Valid ReporEstoqueRequest request) {
        return ResponseEntity.ok(estoqueService.reporEstoque(produtoId, request));
    }

    @Operation(summary = "Cadastra estoque de um produto em uma unidade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estoque cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "404", description = "Produto ou unidade não encontrado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @PostMapping("/cadastra")
    public ResponseEntity<EstoqueResponse> cadastraEstoque(@RequestBody EstoqueRequest request) {
        return ResponseEntity.status(201).body(estoqueService.adicionaEstoque(request));
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
        return ResponseEntity.status(400)
                .body(new ErroResponse(400, "Requisição inválida: " + ex.getMostSpecificCause().getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
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
