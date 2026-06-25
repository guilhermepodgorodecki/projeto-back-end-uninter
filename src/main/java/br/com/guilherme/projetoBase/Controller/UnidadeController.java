package br.com.guilherme.projetoBase.Controller;

import br.com.guilherme.projetoBase.DTO.UnidadeDto;
import br.com.guilherme.projetoBase.DTO.UnidadeRequest;
import br.com.guilherme.projetoBase.DTO.UnidadeResponse;
import br.com.guilherme.projetoBase.Service.UnidadeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/unidade")
@RequiredArgsConstructor
public class UnidadeController {

    private final UnidadeService unidadeService;

    @Operation(summary = "Cadastra uma nova unidade")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Unidade cadastrada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @PostMapping("/cadastra")
    public ResponseEntity<UnidadeResponse> cadastraUnidade(@RequestBody UnidadeRequest request) {
        return ResponseEntity.status(201).body(unidadeService.criaUnidade(request));
    }

    @Operation(summary = "Lista todas as unidades")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
            @ApiResponse(responseCode = "401", description = "Não autorizado"),
            @ApiResponse(responseCode = "403", description = "Acesso negado"),
            @ApiResponse(responseCode = "500", description = "Erro no servidor")
    })
    @PreAuthorize("hasAnyRole('ADMIN', 'GERENTE')")
    @GetMapping("/busca")
    public ResponseEntity<List<UnidadeDto>> getUnidades() {
        return ResponseEntity.ok(unidadeService.getTodas());
    }
}
