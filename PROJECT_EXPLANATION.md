# **NykaaSandbox Project Explanation for Interview**

## **Project Overview**
NykaaSandbox is a **B2B e-commerce platform** built for Nykaa that enables third-party sellers (both Dropship and JIT sellers) to integrate and manage their products, inventory, and orders through REST APIs.

---

## **Technology Stack**
- **Backend**: Spring Boot 3.2.3 with Java 17
- **Database**: MySQL 8.0 (primary) + H2 (in-memory for testing)
- **Caching**: Redis (for token management)
- **ORM**: Spring Data JPA / Hibernate
- **API Documentation**: Swagger/OpenAPI via SpringDoc
- **Build Tool**: Maven
- **Key Libraries**: 
  - Lombok (boilerplate reduction)
  - MapStruct (object mapping)
  - Bucket4j (rate limiting)
  - iTextPDF (PDF generation)

---

## **Core Modules & Architecture**

### **1. Authentication & Authorization** 🔐
**File**: `NyAuthController.java`, `NyAuthService.java`, `TokenCacheService.java`

- **Purpose**: Manages seller authentication via username/password
- **Key Feature**: Redis-based token caching with 24-hour TTL
- **Flow**:
  1. Seller logs in → receives auth token
  2. Token stored in Redis with key: `auth:token:{username}`
  3. All subsequent API calls use `apiKey` header containing this token
  4. Thread-safe operations with validation against injection attacks

```java
// TokenCacheService.java
public void storeToken(String username, String token) {
    redisTemplate.opsForValue().set(
        buildKey(username),
        token,
        Duration.ofHours(24)  // TTL
    );
}
```

---

### **2. Product Management** 📦
**Files**: `NyProductController.java`, `NyProductServiceImpl.java`, `NyProductRepository.java`

**Endpoints**:
1. **`POST /productCreate`**: Bulk product creation
2. **`GET /productFetch`**: Fetch products with filters and pagination

**Key Features**:
- **Multi-tenant Support**: Products isolated per seller via `token`
- **Pessimistic Locking**: `@Lock(LockModeType.PESSIMISTIC_WRITE)` prevents concurrent updates
- **Pagination**: Custom implementation with `PageRequest`
- **Filtering**: By `updatedDate` and/or `skuCode`
- **Validation**: Bean Validation (`@NotBlank`, `@Pattern` for prices)

**Database Schema**:
```sql
CREATE TABLE ny_products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(255) NOT NULL,
    token VARCHAR(255) NOT NULL,  -- Multi-tenant isolation
    sku_name VARCHAR(255) NOT NULL,
    mrp VARCHAR(255) NOT NULL,
    sale_price VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL,
    -- + 10+ product attributes
    UNIQUE KEY uk_sku_token (sku, token)  -- Composite unique constraint
);
```

**Challenge Solved**: Handling bulk creation with duplicate SKU detection and rollback on failure.

---

### **3. Inventory Management** 📊
**Files**: `InventoryController.java`, `InventoryServiceImpl.java`

**Endpoint**: `POST /inventoryPriceUpdate`

**Features**:
- **Batch Updates**: Process multiple SKUs in one request
- **Transaction Management**: `@Transactional` ensures atomicity
- **Error Handling**: Continue processing even if some SKUs fail
- **Audit Trail**: Logs all updates with status (SUCCESS/ERROR)

**Response Codes**:
- `0`: All SKUs updated successfully
- `304`: Partial success (some SKUs failed)
- `404`: Product not found
- `500`: Internal error

```java
// Example: Update inventory for multiple SKUs
for (InventoryItemDTO item : requestDTO.getInvPriceList()) {
    try {
        validateAndUpdateInventory(item, token, skuResponses);
    } catch (ProductNotFoundException e) {
        errors.add("SKU not found: " + item.getSku());
        // Continue processing other items
    }
}
```

---

### **4. Order Management** 🛒
**Files**: `OrderController.java`, `OrderServiceImpl.java`, `OrderRepository.java`

**Endpoints**:
1. **`POST /orderList`** & `/orderListJIT`: Fetch orders with filters
2. **`POST /orderFetch`** & `/orderFetchJIT`: Get detailed order information

**Seller Types**:
- **DROPSHIP**: Products held by seller, shipped directly to customers
- **JIT (Just-In-Time)**: Products held by Nykaa warehouse

**Key Features**:
- **Complex Filtering**: By `updateDate`, `status`, `sellerId`
- **Pagination**: Server-side with configurable page size
- **Null-Safe Data Mapping**: Latest update ensures no null values in responses
- **Order-Item Relationship**: One-to-many mapping with item-level tracking

**Database Design**:
```sql
-- Orders table
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(255) NOT NULL UNIQUE,
    order_status VARCHAR(50) NOT NULL,
    seller_type VARCHAR(50),  -- DROPSHIP or JIT
    seller_id VARCHAR(255) NOT NULL,
    -- + 40+ order fields (billing, shipping, payment, etc.)
    INDEX idx_seller_type_seller_id (seller_type, seller_id)
);

-- Order Items table (one-to-many)
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(255) NOT NULL,
    sku_code VARCHAR(255),
    order_qty VARCHAR(50),
    -- + 20+ item fields (pricing, shipping, tracking, etc.)
    INDEX idx_order_no (order_no)
);
```

**Recent Enhancement**: 
I just implemented **null-safe data transformation** to ensure API responses always return non-null values (empty strings or "0" for numeric fields), preventing null pointer exceptions and improving API consistency.

```java
// OrderServiceImpl.java - Latest update
private String safe(String value) {
    return value != null ? value : "";
}

private String safeZero(String value) {
    return value != null ? value : "0";
}
```

---

## **Design Patterns & Best Practices**

### **1. Layered Architecture** 🏗️
```
Controller → Service → Repository → Entity
     ↓          ↓          ↓           ↓
   DTOs    Business    Database    JPA
            Logic      Queries
```

### **2. Dependency Injection** 🔄
- Constructor-based injection (`@RequiredArgsConstructor` from Lombok)
- No `@Autowired` fields to improve testability

### **3. Exception Handling** ⚠️
- **Global Handler**: `@RestControllerAdvice` catches all exceptions
- **Custom Exceptions**: `ProductNotFoundException`, `InventoryUpdateException`, `DuplicateProductException`
- **Proper HTTP Status Codes**: 404 for not found, 304 for partial success, 500 for server errors

```java
@ExceptionHandler(ProductNotFoundException.class)
public ResponseEntity<InventoryPriceUpdateResponseDTO> handleProductNotFoundException(
    ProductNotFoundException ex) {
    // Return 404 with proper error message
}
```

### **4. Database Optimization** 🚀
- **Indexes**: Strategic indexing on frequently queried columns
- **Connection Pooling**: HikariCP with optimized settings
- **Query Optimization**: Custom JPQL queries with null-safe conditions
- **Pessimistic Locking**: Prevents lost updates on products

---

## **Testing & Quality**

### **Test Data**
Comprehensive seed data in `data.sql`:
- 10+ Dropship orders with complete customer/address/payment details
- 5+ JIT orders
- 20+ order items across various order statuses
- Products with realistic pricing and inventory

### **Logging** 📝
- **SLF4J + Logback**: Structured logging
- **Log Levels**: DEBUG for development, INFO for production
- **Audit Trail**: All inventory updates logged with full context

---

## **Configuration Management**

### **application.properties**
- **Database**: MySQL connection with HikariCP pooling
- **Redis**: Local caching configuration
- **JPA**: Hibernate dialect, DDL auto-update
- **Schema Init**: Auto-run `schema.sql` and `data.sql` on startup

---

## **Challenges Solved** 💪

1. **Multi-Tenancy**: Secure data isolation using composite keys (sku + token)
2. **Bulk Operations**: Handling batch updates with partial failures
3. **Data Consistency**: Transaction management for inventory updates
4. **Performance**: Pagination, indexing, connection pooling
5. **Null Safety**: Implemented helper methods to prevent null responses
6. **Concurrency**: Pessimistic locking on product updates
7. **API Versioning**: `/v3` indicates this is the third version of the API

---

## **Future Enhancements** 🔮
- Implement rate limiting with Bucket4j
- Add PDF invoice generation (iText dependencies already included)
- Implement product image upload service
- Add webhooks for order status notifications
- Implement GraphQL API for flexible queries
- Add comprehensive unit and integration tests

---

## **Walkthrough Script for Interview**

**Slide 1: Introduction**
"NykaaSandbox is a B2B e-commerce platform I built for Nykaa, enabling third-party sellers to manage their products, inventory, and orders. It's a Spring Boot microservice following REST principles."

**Slide 2: Architecture**
"Let me walk through the layered architecture. Controllers handle HTTP requests and response mapping. Services contain business logic. Repositories manage database operations with JPA. This separation ensures maintainability and testability."

**Slide 3: Product Management**
"Here's the product creation flow. Notice the pessimistic locking to prevent concurrent modifications. The composite unique constraint on (sku, token) ensures multi-tenant isolation."

**Slide 4: Inventory & Orders**
"The inventory update uses transaction management, and the recent enhancement I made ensures null-safe data transformation, preventing API responses with null values."

**Slide 5: Authentication**
"Redis caching provides fast token lookup with TTL expiration. This thread-safe implementation prevents injection attacks through input validation."

**Conclusion**: "This project demonstrates my understanding of Spring Boot, database design, REST APIs, and production-ready code with proper error handling, logging, and testing."

---

## **Key Takeaways for Interview**
✅ **Microservices Architecture**: Modular, scalable design  
✅ **Database Design**: Proper normalization, indexing, relationships  
✅ **RESTful APIs**: Clean endpoints with proper HTTP methods  
✅ **Error Handling**: Comprehensive exception management  
✅ **Security**: Multi-tenancy, input validation, token management  
✅ **Performance**: Caching, connection pooling, query optimization  
✅ **Best Practices**: Dependency injection, logging, code organization  
✅ **Problem-Solving**: Handling edge cases (null values, bulk operations)  
