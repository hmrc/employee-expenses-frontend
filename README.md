# Employee Expenses Frontend 

## Info

This service is also known as *Claim for your work uniform and tools*

This service allows an individual to update their tax accounts flat rate expense,
specifically that of their 56 IABD code relating to uniform and tool expenses.

Half of this service is unauthenticated, asking a number of eligibility questions to ensure a user is able to claim before passing them through to GG for sign in and completion.

This service does not have it's own backend for updating NPS, instead it uses TAI for this integration.

### Dependencies

|Service        |Link                                   |
|---------------|---------------------------------------|
|Tai            |https://github.com/hmrc/tai            |
|Citizen Details|https://github.com/hmrc/citizen-details|

### Endpoints used

|Service        |HTTP Method |Route                                  |Purpose |
|---------------|--- |----------------|----------------------------------|
|Tai            |GET |/tai/${nino}/tax-account/${year} /expenses/flat-rate-expenses| Returns details of a users tax account specifically that of IABD 56 |
|Tai            |POST|/tai/${nino}/tax-account/${year} /expenses/flat-rate-expenses| Updates a users tax account specifically that of IABD 56  |
|Citizen Details|GET |/citizen-details/${nino}/etag|retrieves the users etag which is added to their update request to NPS to ensure optimistic locking|

## Running the service

Service Manager: EE_ALL

Port: 9334

## Tests and prototype

Employee expenses prototype can be found [here](https://employee-expenses.herokuapp.com/)

|Repositories     |Link                                                                   |
|-----------------|-----------------------------------------------------------------------|
|Journey tests    |https://github.com/hmrc/employee-expenses-journey-tests                |
|Performance tests|https://github.com/hmrc/employee-expenses-performance-tests            |
|Prototype        |https://github.com/hmrc/employee-expenses-prototype                    |
