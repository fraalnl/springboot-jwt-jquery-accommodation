Feature: Authentication

Background:
  * url 'http://localhost:8082/api/auth'
  
Scenario: Login with valid credentials as admin
  Given path 'login'
  And request { username: 'admin', password: 'admin123' }
  When method post
  Then status 200
  * def token = response.token
  * def result = { token: token }
  * print 'Generated Token:', result.token
  * karate.set('token', result.token)

Scenario: Login with invalid credentials
  Given path 'login'
  And request { username: 'admin', password: 'wrongpassword' }
  When method post
  Then status 401
  And match response.error == 'Invalid credentials. Please try again.'
