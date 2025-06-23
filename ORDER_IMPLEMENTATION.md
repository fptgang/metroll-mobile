# Order API Implementation

Implementation of Order management system endpoints based on OpenAPI specification. This includes checkout functionality, order retrieval, and payment processing up to the use case layer.

## Completed Tasks

- [x] Task list created
- [x] Set up domain models and entities
- [x] Create repository interfaces in domain layer
- [x] Create Order domain entity
- [x] Create OrderDetail domain entity
- [x] Create CheckoutRequest domain model
- [x] Create CheckoutItem domain model
- [x] Create OrderStatus enum
- [x] TicketType enum (already existed)
- [x] PageDto generic class (already existed)
- [x] Create OrderRepository interface
- [x] Create PaymentRepository interface
- [x] Create CheckoutUseCase
- [x] Create GetAllOrdersUseCase
- [x] Create GetOrderByIdUseCase
- [x] Create GetMyOrdersUseCase
- [x] Create GetPaymentStatusUseCase
- [x] Create HandlePaymentSuccessUseCase
- [x] Create HandlePaymentCancelUseCase
- [x] Create Order API data transfer objects (DTOs)
  - [x] OrderDto
  - [x] OrderDetailDto
  - [x] CheckoutRequestDto
  - [x] CheckoutItemRequestDto
- [x] Create Order API service interface
- [x] Create Payment API service interface
- [x] Create OrderRepository implementation
- [x] Create PaymentRepository implementation
- [x] Create domain to DTO mappers
- [x] Add Order and Payment APIs to RetrofitServer

## In Progress Tasks

- [x] Set up dependency injection bindings

## Future Tasks

### Server Integration (core/data)

- [ ] Add proper error handling with ServerFlow (✅ already implemented)
- [ ] Add authentication headers for secured endpoints
- [ ] Configure pagination parameters (✅ already implemented)
- [ ] Handle payment webhook processing (✅ already implemented)

### Server Integration (core/data)

- [ ] Add Order API endpoints to RetrofitServer
- [ ] Implement proper error handling with ServerFlow
- [ ] Add authentication headers for secured endpoints
- [ ] Configure pagination parameters
- [ ] Handle payment webhook processing

## Implementation Plan

### Architecture Overview

The Order API implementation follows the clean architecture pattern:

```
Server --> OrderRemoteDataSource |
                                |--> OrderRepository --> OrderUseCases --> UI
Local -->  OrderLocalDataSource  |
```

### Data Flow

1. **Checkout Flow**: CheckoutUseCase → OrderRepository → OrderRemoteDataSource → API
2. **Order Retrieval**: GetOrdersUseCase → OrderRepository → OrderRemoteDataSource → API
3. **Payment Processing**: PaymentUseCases → PaymentRepository → PaymentRemoteDataSource → API

### API Endpoints to Implement

1. **POST /checkout** - Create order and process payment (Authenticated)
2. **GET /orders** - Get all orders with pagination (Admin/Staff only)
3. **GET /orders/{orderId}** - Get specific order details (Authenticated)
4. **GET /my-orders** - Get current user's orders with pagination (Authenticated)
5. **GET /payment/status/{orderId}** - Get payment status
6. **GET /payment/success** - Handle payment success callback
7. **GET /payment/cancel** - Handle payment cancellation
8. **POST /payment/webhook** - Handle payment webhook

### Domain Models Structure

```kotlin
// Order Entity
data class Order(
    val id: String,
    val staffId: String?,
    val customerId: String,
    val discountPackage: String?,
    val voucher: String?,
    val baseTotal: Double,
    val discountTotal: Double,
    val finalTotal: Double,
    val paymentMethod: String,
    val status: OrderStatus,
    val transactionReference: String?,
    val paymentUrl: String?,
    val qrCode: String?,
    val orderDetails: List<OrderDetail>,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
)

// OrderDetail Entity
data class OrderDetail(
    val id: String,
    val ticketOrderId: String,
    val ticketType: TicketType,
    val p2pJourney: String?,
    val timedTicketPlan: String?,
    val quantity: Int,
    val unitPrice: Double,
    val baseTotal: Double,
    val discountTotal: Double,
    val finalTotal: Double,
    val createdAt: LocalDateTime
)

// Enums
enum class OrderStatus { PENDING, COMPLETED, FAILED }
enum class TicketType { P2P, TIMED }
```

### Security Considerations

- All order-related endpoints require authentication (bearerAuth)
- Admin/Staff role verification for `/orders` endpoint
- User authorization for accessing specific orders
- Secure payment webhook handling

### Error Handling

- Network connectivity issues
- Authentication/Authorization failures
- Payment processing errors
- Order not found scenarios
- Invalid request parameters

### Testing Strategy

- Unit tests for use cases
- Repository implementation tests
- API service integration tests
- Error scenario testing
- Authentication flow testing

## Relevant Files

### Domain Layer Files
- `core/domain/src/main/java/com/vidz/domain/model/Order.kt` - Order entity
- `core/domain/src/main/java/com/vidz/domain/model/OrderDetail.kt` - OrderDetail entity
- `core/domain/src/main/java/com/vidz/domain/model/CheckoutRequest.kt` - Checkout request model
- `core/domain/src/main/java/com/vidz/domain/model/CheckoutItem.kt` - Checkout item model
- `core/domain/src/main/java/com/vidz/domain/model/OrderStatus.kt` - Order status enum
- `core/domain/src/main/java/com/vidz/domain/model/TicketType.kt` - Ticket type enum
- `core/domain/src/main/java/com/vidz/domain/repository/OrderRepository.kt` - Order repository interface
- `core/domain/src/main/java/com/vidz/domain/repository/PaymentRepository.kt` - Payment repository interface

### Use Case Files
- `core/domain/src/main/java/com/vidz/domain/usecase/order/CheckoutUseCase.kt` - Checkout functionality
- `core/domain/src/main/java/com/vidz/domain/usecase/order/GetAllOrdersUseCase.kt` - Get all orders
- `core/domain/src/main/java/com/vidz/domain/usecase/order/GetOrderByIdUseCase.kt` - Get order by ID
- `core/domain/src/main/java/com/vidz/domain/usecase/order/GetMyOrdersUseCase.kt` - Get user orders
- `core/domain/src/main/java/com/vidz/domain/usecase/payment/GetPaymentStatusUseCase.kt` - Payment status
- `core/domain/src/main/java/com/vidz/domain/usecase/payment/HandlePaymentSuccessUseCase.kt` - Payment success
- `core/domain/src/main/java/com/vidz/domain/usecase/payment/HandlePaymentCancelUseCase.kt` - Payment cancel

### Data Layer Files
- `core/data/src/main/java/com/vidz/data/dto/order/OrderDto.kt` - Order DTO
- `core/data/src/main/java/com/vidz/data/dto/order/OrderDetailDto.kt` - OrderDetail DTO
- `core/data/src/main/java/com/vidz/data/dto/order/CheckoutRequestDto.kt` - Checkout request DTO
- `core/data/src/main/java/com/vidz/data/dto/order/CheckoutItemRequestDto.kt` - Checkout item DTO
- `core/data/src/main/java/com/vidz/data/dto/common/PageDto.kt` - Generic page DTO
- `core/data/src/main/java/com/vidz/data/api/OrderApiService.kt` - Order API interface
- `core/data/src/main/java/com/vidz/data/datasource/order/OrderRemoteDataSource.kt` - Order remote data source
- `core/data/src/main/java/com/vidz/data/datasource/payment/PaymentRemoteDataSource.kt` - Payment remote data source
- `core/data/src/main/java/com/vidz/data/repository/OrderRepositoryImpl.kt` - Order repository implementation
- `core/data/src/main/java/com/vidz/data/repository/PaymentRepositoryImpl.kt` - Payment repository implementation
- `core/data/src/main/java/com/vidz/data/mapper/OrderMapper.kt` - Order domain-DTO mapper
- `core/data/src/main/java/com/vidz/data/mapper/PaymentMapper.kt` - Payment domain-DTO mapper 