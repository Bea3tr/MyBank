Server / Client program emulating a bank interface

[Server] bank.Server
Read input from client to determine method to call
1. get client's name
2. check if client is registered
    - true: ask for pin
    - false: ask if client wants to create an account
        - true: create account - name & pin
        - false: exit program
3. check if pin is correct
    - true: login user & start operation
    - false: try again (max attempt: 3); exceed attempts - exit program

[Client] bank.Client localhost:3000
Output option to Server
Get input from Server

[Bank]
- Members: name, pin, account balance, account number
- Methods: deposit, withdraw, getBalance, save