# CA-BH-Customer-service

Service is designed to work with customers

## API
**POST /api/v1/customer/all** - Fetch all customers in system

Response example:
```json
[
  {
    "id": "9818df38-f299-4af7-a8db-3769e88ac26c",
    "name": "First_name",
    "surname": "First_surname"
  },
  {
    "id": "9bd7d7b7-08b9-4c71-ba4b-ad17d8cde09b",
    "name": "Second_name",
    "surname": "Second_surname"
  }
]
```

**POST /api/v1/customer** - Create account for customer

Request example:
```json
{ 
  "customerId": "2d2ddda5-9ad5-4917-a280-405d220868aa", 
  "initialCredit": 0.3
}
```

**GET /api/v1/customer/{customerId}** - Get customer account details

Response example:
```json
{
  "name": "Name",
  "surname": "Surname",
  "balance": 10.00,
  "accounts": [
    {
      "number": "BE30 2300 0000 0021",
      "balance": 10.00,
      "transactions": [
        {
          "amount": 10.00,
          "createdAt": "2021-04-14T17:16:07.36689Z"
        }
      ]
    }
  ]
}
```
