Feature: Admin Room Operations

Background:
  * url 'http://localhost:8082/api/accommodation'
  # * def tokenResponse = karate.callSingle('classpath:edu/tus/controller/auth.feature', { username: 'admin', password: 'admin123' })
  # * def token = tokenResponse.token
  # * print 'Token from auth:', token
  # Build the header value separately:
  # * def headerValue = 'Bearer ' + token
  # Then configure headers using #() to inject the string:
  # * configure headers = { Authorization: '#(headerValue)' }

Scenario: List all rooms
  Given path 'rooms'
  When method get
  Then status 200
  And match response._embedded.rooms != null
