## Eshopping module

### Please note
- This is demo
- Because time is limited (only 4 days), I try to show my knowledge as much as I can
- Still have many TODO, I haven't done yet

### Technology
- Spring boot
- Discovery service: Eureka
- Storage: Mysql
- Message Queue: Kafka
- Log: ELK (**reuse my old ELK stack I built before**)
- Others: Docker, Archaius (external configuration) , Liquid base (database changelog), Rx, auth0 (JWT token library)

### Application architecture
- Eshopping module: the module allow customer buy prepaid data without login
- Billing service: the service verify card, account and charge customer
- Telecom provider: this is third party
> Note: Currently client call directly to eshopping api. If have more time I will create API gateway use Netflix Zull to client call our system 

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/architecture.JPG)

#### Description
- Eshopping module, Billing service are same network and register to eureka server
- Telecom provider is another network
- Eshopping module call Telecom provider with JWT token to authenicate each other

#### Source description
- In all modules/services, I follow component based architecture

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/component-base-example.JPG)

- I put helper classes in base package to share for multiple modules/services

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/base-class-example.JPG)

- I put infrastructure in infra package

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/infra-example.JPG)

### Flow

#### Purchase voucher

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/purchase-flow.JPG)

#### Query voucher

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/query-flow.JPG)

#### Description
- I used **two-phase commit pattern** to process purchase

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/2-phase-commit.JPG)

- I used kafka as message queue in case sending sms

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/send-sms.JPG)

- There are cron job to clean expired order/transaction (**not yet implement inside**)

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/cron-job.JPG)

- To solve timeout problem (120s) when calling to third party, I **set connection timeout in request to third party is 120s** then I created a **Observable subscribe on a thread pool executor and blocking with timeout 30s** (following requirement). If timeout 30s reach, respond to client **waiting for sms**, and thread which call to third party keep running and send sms after done.
> We can use other solution if third party support other pattern. I will talk later when take the interview.

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/timeout-30.JPG)

![Architecture](https://github.com/phuonghien90/eshopping/raw/master/imgs/timeout-120.JPG)

### Setup eviroment and deploy
``` 
$ cd setup 
$ # set up all in one : docker, mysql, eureka, zookeeper, kafka
$ ./setup.sh
$ # deploy up all in one : eshopping, billing, telecom
$ ./deploy.sh
```

### CURL
##### Purchase API
```
curl --location --request POST 'http://139.180.188.190:8081/api/v1/purchase' \
--header 'Content-Type: application/json' \
--data-raw '{
    "phoneNumber": "84905586823",
    "skuCode": "123",
    "bankCard": {
        "number": "342",
        "expDate": "2025-12",
        "cvv": "234"
    }
}'
```

##### Verify Otp API
```
curl --location --request POST 'http://139.180.188.190:8081/api/v1/verify' \
--header 'token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJvcmRlcklkIjoxMDAwMDA1LCJpc3MiOiJlc2hvcHBpbmciLCJleHAiOjE2MDk2ODUxODN9.Jv2z2o_X_R_plKD-8UbSGVT8VeaM3LuTzt_0_KGf304' \
--header 'Content-Type: application/json' \
--data-raw '{
    "otp": "999"
}'
```

##### Verify Phone Number to query voucher
```
curl --location --request POST 'http://139.180.188.190:8081/api/v1/query/verify-phone-number' \
--header 'Content-Type: application/json' \
--data-raw '{
    "phoneNumber": "84905586823"
}'
```

##### Query voucher
```
curl --location --request POST 'http://139.180.188.190:8081/api/v1/query/voucher' \
--header 'token: eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJwaG9uZU51bWJlciI6Ijg0OTA1NTg2ODIzIiwiaXNzIjoiZXNob3BwaW5nIiwiZXhwIjoxNjA5ODExOTk0fQ.ycFIYdeOnA1o3RMKlUCbFjons4IbxQrxSEiYipwt1t4' \
--header 'Content-Type: application/json' \
--data-raw '{
    "otp": "999"
}'
```