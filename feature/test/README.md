# Order API Test Screen Usage Guide

## 🚀 How to Access the Test Screen

### Method 1: Deep Link Navigation
You can navigate directly to the test screen using the navigation system. Add this code in any composable where you want to create a test button:

```kotlin
import com.vidz.test.ROOT_TEST_ROUTE

// In your composable:
Button(
    onClick = { 
        navController.navigate(ROOT_TEST_ROUTE)
    }
) {
    Text("Open API Test Screen")
}
```

### Method 2: Add to Bottom Navigation (Temporary)
For easier access during development, you can temporarily add it to the bottom navigation in `MetrollAppViewModel.kt`:

```kotlin
// Add this to customerRoutes list:
"test_root"
```

## 🧪 How to Use the Test Screen

### 1. **Test Checkout (Create Order)**
- Click **"Test Checkout"** button
- This creates a sample order with:
  - 2 P2P tickets (test-p2p-journey-1)
  - Payment method: CREDIT_CARD
  - Customer ID: test-customer-123
- **Expected Result**: ✅ Success with new Order ID

### 2. **Test Get All Orders (Admin/Staff)**
- Click **"Get All Orders"** button
- Retrieves all orders with pagination (page 0, size 10)
- **Expected Result**: ✅ List of orders with count

### 3. **Test Get My Orders (User)**
- Click **"Get My Orders"** button
- Retrieves current user's orders with pagination
- **Expected Result**: ✅ User's orders with count

### 4. **Test Order by ID**
- Enter an **Order ID** in the text field
- Click **"Get Order By ID"** button
- **Expected Result**: ✅ Specific order details

### 5. **Test Payment Status**
- Enter an **Order ID** in the text field
- Click **"Get Payment Status"** button
- **Expected Result**: ✅ Payment status string

### 6. **Test Payment Success**
- Enter an **Order ID** in the text field
- Click **"Payment Success"** button
- Simulates successful payment callback
- **Expected Result**: ✅ Payment success confirmation

### 7. **Test Payment Cancel**
- Enter an **Order ID** in the text field
- Click **"Payment Cancel"** button
- Simulates payment cancellation
- **Expected Result**: ✅ Payment cancel confirmation

## 📊 Understanding Results

### Success Indicators
- **✅ Green background**: API call succeeded
- **Order ID displayed**: For successful order creation
- **Count information**: For list operations

### Error Indicators
- **❌ Red background**: API call failed
- **Error message**: Specific error description
- **Common errors**: Network issues, authentication, validation

## 🔧 API Endpoints Being Tested

| Button | HTTP Method | Endpoint | Description |
|--------|-------------|----------|-------------|
| Test Checkout | POST | `/checkout` | Create order and process payment |
| Get All Orders | GET | `/orders` | Get all orders (Admin/Staff) |
| Get My Orders | GET | `/my-orders` | Get current user's orders |
| Get Order By ID | GET | `/orders/{id}` | Get specific order details |
| Get Payment Status | GET | `/payment/status/{id}` | Get payment status |
| Payment Success | GET | `/payment/success` | Handle payment success |
| Payment Cancel | GET | `/payment/cancel` | Handle payment cancellation |

## 🛠️ Troubleshooting

### Authentication Required
Most endpoints require authentication. Make sure you're logged in before testing.

### Network Errors
- Check your internet connection
- Verify server is running
- Check API base URL configuration

### Order ID Not Found
- Use "Test Checkout" first to create an order
- Copy the Order ID from the success result
- Paste it in the Order ID field for other tests

### Server Errors
- Check server logs for detailed error information
- Verify API implementation matches the OpenAPI spec
- Ensure database is properly configured

## 💡 Tips for Testing

1. **Start with Checkout**: Always test checkout first to get valid Order IDs
2. **Copy Order IDs**: Copy successful order IDs for testing other endpoints
3. **Clear Results**: Use the clear button to reset results between tests
4. **Check Loading States**: Loading indicators show when requests are in progress
5. **Test Different Scenarios**: Try with valid and invalid Order IDs to test error handling

## 🔒 Security Notes

- All order endpoints require authentication
- `/orders` endpoint requires Admin/Staff role
- Payment webhooks should only be called by payment providers
- Order IDs should be properly validated server-side 