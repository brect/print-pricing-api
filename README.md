# Print Pricing API

API REST em Kotlin + Spring Boot para precificacao de produtos feitos com impressao 3D.

Objetivo principal:
- manter cadastro de produtos, impressoras, materiais, consumiveis, despesas e marketplaces;
- simular custo e preco de venda por cenario;
- guardar historico de simulacoes para comparacao.

## Stack
- Kotlin 2.2.21
- Spring Boot 4.0.5
- Java 21
- Spring Data JPA
- Spring Security + JWT
- H2 Database
- SpringDoc OpenAPI

## Como executar

### 1) Requisitos
- Java 21

### 2) Subir a aplicacao
```bash
cd /Users/brlimas/Documents/Projetos/backend/print-pricing-api
./gradlew bootRun
```

### 3) URLs locais
- API Base: `http://localhost:8080/api`
- Swagger UI: `http://localhost:8080/api/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/api/v3/api-docs`
- H2 Console: `http://localhost:8080/api/h2-console`

## Autenticacao e autorizacao
A API usa JWT Bearer Token.

Fluxo:
1. criar usuario (`POST /api/users`) ou usar admin bootstrap;
2. autenticar em `POST /api/users/login`;
3. enviar `Authorization: Bearer <token>` nas rotas protegidas.

Usuario bootstrap (criado automaticamente):
- email: `admin@authserver.com`
- senha: `admin`

Regras de token:
- `USER`: expira em 48 horas
- `ADMIN`: expira em 1 hora

### Swagger (importante)
No botao `Authorize`, cole somente o token JWT (sem prefixo `Bearer `).

## Perfis de acesso
- `ADMIN`:
  - CRUD de marketplaces
  - CRUD de despesas/ativos
  - gerenciamento de roles
  - adicionar role em usuario
- `USER` e `ADMIN`:
  - CRUD de products, printers, materials, consumables
  - simulacoes de precificacao
- rotas publicas:
  - `POST /api/users`
  - `POST /api/users/login`
  - Swagger/OpenAPI e H2 Console

## Estrutura de modulos
- `users`: cadastro/login e dados do usuario autenticado
- `roles`: gerenciamento de perfis de acesso
- `products`: produto fisico precificavel
- `printers`: dados tecnicos e custo de maquina
- `materials`: filamentos
- `consumables`: catalogo de consumiveis
- `expenses`: despesas fixas e ativos fixos para rateio
- `marketplaces`: canais e regras de taxa
- `pricing`: simulacao e historico de precificacao
- `security`: JWT e filtros
- `exceptions`: padrao unico de erros

## Relacionamentos de entidades

```mermaid
erDiagram
    PRODUCT ||--o{ PRICING_SIMULATION : "has"
    PRINTER ||--o{ PRICING_SIMULATION : "used_in"
    FILAMENT_MATERIAL ||--o{ PRICING_SIMULATION : "used_in"

    PRICING_SIMULATION ||--o{ PRICING_SIMULATION_CONSUMABLE : "snapshots"
    CONSUMABLE ||..o{ PRICING_SIMULATION_CONSUMABLE : "logical_ref_by_id"

    PRICING_SIMULATION ||--o{ PRICING_SIMULATION_MARKETPLACE_RESULT : "results"
    MARKETPLACE ||--o{ MARKETPLACE_FEE_RULE : "rules"
    MARKETPLACE ||--o{ PRICING_SIMULATION_MARKETPLACE_RESULT : "applied_in"

    USER ||--o{ USER_ROLES : "element_collection"
    ROLE ||..o{ USER_ROLES : "logical_catalog"
```

Legenda:
- `||--o{` = relacionamento JPA/FK.
- `||..o{` = referencia logica (sem FK ORM direto).

## Exemplos de uso

### 1) Criar usuario
`POST /api/users`
```json
{
  "email": "user@example.com",
  "password": "Senha@123",
  "name": "Usuario Teste"
}
```

### 2) Login
`POST /api/users/login`
```json
{
  "email": "user@example.com",
  "password": "Senha@123"
}
```
Resposta:
```json
{
  "token": "<jwt>",
  "user": {
    "id": 2,
    "email": "user@example.com",
    "name": "Usuario Teste",
    "roles": ["USER"]
  }
}
```

### 3) Product (USER ou ADMIN)
`POST /api/products`
```json
{
  "name": "Suporte de controle",
  "sku": "SUP-CTRL-01",
  "description": "Suporte para controle de videogame",
  "defaultWeightGrams": 85,
  "defaultPrintMinutes": 210
}
```

### 4) Printer (USER ou ADMIN)
`POST /api/printers`
```json
{
  "name": "Ender 3 S1 Plus",
  "purchasePrice": 3800,
  "maintenanceCost": 950,
  "usefulLifeHours": 5000,
  "consumptionKw": 0.3
}
```

### 5) Material (USER ou ADMIN)
`POST /api/materials`
```json
{
  "brand": "PLA Generico",
  "type": "PLA",
  "spoolCost": 100,
  "spoolWeightKg": 1,
  "color": "Branco"
}
```

### 6) Consumivel (USER ou ADMIN)
`POST /api/consumables`
```json
{
  "name": "Embalagem kraft",
  "unitCost": 2.5
}
```

### 7) Marketplace (ADMIN)
`POST /api/marketplaces`
```json
{
  "name": "Shopee",
  "active": true,
  "feeRules": [
    {
      "name": "Comissao padrao",
      "type": "PERCENTAGE",
      "percentage": 20,
      "fixedAmount": 0
    }
  ]
}
```

### 8) Despesa fixa (ADMIN)
`POST /api/expenses/fixed`
```json
{
  "name": "Aluguel",
  "monthlyAmount": 1200,
  "allocationStrategy": "PER_MACHINE_HOUR",
  "active": true,
  "monthlyUnitsCapacity": 300,
  "monthlyMachineHoursCapacity": 160
}
```

### 9) Ativo fixo (ADMIN)
`POST /api/expenses/assets`
```json
{
  "name": "Notebook de modelagem",
  "cost": 6000,
  "usefulLifeMonths": 36,
  "allocationStrategy": "PER_MACHINE_HOUR",
  "active": true,
  "monthlyUnitsCapacity": 300,
  "monthlyMachineHoursCapacity": 160
}
```

### 10) Simulacao de precificacao (USER ou ADMIN)
`POST /api/pricing/simulations`
```json
{
  "name": "Cenario Shopee - PLA",
  "notes": "Simulacao com despesas alocadas",
  "productId": 1,
  "printerId": 1,
  "materialId": 1,
  "weightGrams": 36,
  "printMinutes": 143,
  "energyKwhCost": 0.95,
  "failureRatePercent": 12,
  "fixedCost": 3.5,
  "laborCost": 8,
  "units": 1,
  "markupMultiplier": 2,
  "taxPercent": 8,
  "consumables": [
    {
      "consumableId": 1,
      "quantity": 1
    }
  ],
  "marketplaceIds": [1]
}
```

Campos relevantes no retorno de custos:
- `material`
- `machineTotal`
- `consumables`
- `fixedExpensesAllocated`
- `fixedAssetsAllocated`
- `fixed` (soma: fixo manual + alocacoes)
- `labor`
- `failures`
- `total`
- `unit`

## Mapa rapido de rotas

### Users
- `POST /api/users` (publico)
- `POST /api/users/login` (publico)
- `GET /api/users/me` (autenticado)
- `PUT /api/users/{id}/roles/{role}` (ADMIN)

### Roles (ADMIN)
- `POST /api/roles`
- `GET /api/roles`

### Products (USER/ADMIN para escrita)
- `POST /api/products`
- `GET /api/products`
- `GET /api/products/{id}`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`

### Printers (USER/ADMIN para escrita)
- `POST /api/printers`
- `GET /api/printers`
- `GET /api/printers/{id}`
- `PUT /api/printers/{id}`
- `DELETE /api/printers/{id}`

### Materials (USER/ADMIN para escrita)
- `POST /api/materials`
- `GET /api/materials`
- `GET /api/materials/{id}`
- `PUT /api/materials/{id}`
- `DELETE /api/materials/{id}`

### Consumables (USER/ADMIN para escrita)
- `POST /api/consumables`
- `GET /api/consumables`
- `GET /api/consumables/{id}`
- `PUT /api/consumables/{id}`
- `DELETE /api/consumables/{id}`

### Marketplaces (ADMIN para escrita)
- `POST /api/marketplaces`
- `GET /api/marketplaces`
- `GET /api/marketplaces/{id}`
- `PUT /api/marketplaces/{id}`
- `DELETE /api/marketplaces/{id}`

### Expenses
- `POST /api/expenses/fixed` (ADMIN)
- `GET /api/expenses/fixed` (autenticado)
- `PUT /api/expenses/fixed/{id}` (ADMIN)
- `DELETE /api/expenses/fixed/{id}` (ADMIN)
- `POST /api/expenses/assets` (ADMIN)
- `GET /api/expenses/assets` (autenticado)
- `PUT /api/expenses/assets/{id}` (ADMIN)
- `DELETE /api/expenses/assets/{id}` (ADMIN)

### Pricing
- `POST /api/pricing/simulations` (autenticado)
- `GET /api/pricing/simulations/{id}` (autenticado)
- `GET /api/pricing/simulations?productId={id}` (autenticado)

## Erros padronizados
A API retorna um contrato unico de erro (`ApiError`) com:
- `timestamp`
- `status`
- `error`
- `message`
- `path`
- `details` (em erros de validacao)

## Documento de dominio
Detalhamento de dominio em: [docs/domain-design.md](docs/domain-design.md)
