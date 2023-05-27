# CASH DISPENSER APPLICATION

## Cash Inventory Resource API

The Cash Inventory Resource provides REST endpoints for managing the cash inventory. Please note there is no Authentication currently.

### Initialize Dispenser

**Endpoint:** `POST /v1/cash-inventory`

**Request Header:**  `Content-Type: application/json`

Initialize the cash dispenser with the provided bill types and their respective number of bills.

Request Body:
```json
{
  "bills": {
    "TWENTY": 100,
    "FIFTY": 50
  },
  "currencyCode": "AUD"
}
```
**Response:**

Status Code: 201 (Created)

Response Body:
```
{
  "bills": {
    "TWENTY": 100,
    "FIFTY": 50
  },
  "totalAmount": 4500,
  "currencyCode": "AUD",
  "transactions": [],
}
```
### Retrieve All Cash

**Endpoint:** `GET /v1/cash-inventory/bills`

**Request Header:**  `Content-Type: application/json`

Retrieve all the cash in the dispenser.

**Response:**

Status Code: 200 (OK)

Response Body:
```
{
  "TWENTY": 100,
   "FIFTY": 50
}
```

### Add Bills

**Endpoint:** `PATCH /v1/cash-inventory`

**Request Header:**  `Content-Type: application/json-patch+json`

Add the bills in the dispenser.

Request Body:
```
{
  "key": "bills",
  "operation": "add",
  "value": {"FIFTY": 10,"TWENTY": 100}
}
```
**Response:**

Status Code: 200 (OK)

Response Body:
```
{
  "TWENTY": 120,
   "FIFTY": 50
}
```

### Remove Bills

**Endpoint:** `PATCH /v1/cash-inventory`

**Request Header:**  `Content-Type: application/json-patch+json`

Remove the bills from dispenser.

Request Body:
```
{
  "key": "bills",
  "operation": "remove",
  "value": {"FIFTY": 10,"TWENTY": 100}
}
```
**Response:**

Status Code: 200 (OK)

Response Body:
```
{
  "TWENTY": 40,
   "FIFTY": 30
}
```
### Perform Transactions

**Endpoint:** `POST /v1/cash-inventory/transactions`

**Request Header:**  `Content-Type: application/json`

Perform a cash transaction to dispense the requested amount.

Request Body:
```
{
  "amount": 440
}
```

**Response:**

Status Code: 200 (OK)

Response Body:
```
{
"TWENTY": 2,
"FIFTY": 8
}
```

## How to build

**Prerequisites:**
 - JDK 17 or higher
 - Maven 3 or higher

**Steps**

- Navigate to product root and run ```mvn clean install```

## How to run

- Navigate to product root and run ```mvn spring-boot:run``` which will start the server in port ```3000```

