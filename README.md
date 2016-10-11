# oauth

# sso-oauth :
project is an oauth2 authorization server supporting password grant type issuing JWT token with standard payload (roles, exp_time, client id). Also provides basic functionalities for managing clients (via /user endpoint - see code). So far, user enpoint is not secured (can be secured easily by uncommenting line 79 in OauthServerApp.java file)
NOTE: project is setup to use Postgres DB; before running app (via maven goal spring-boot:run), you need to install Postgres DB with initialized database and then change connection string application.properties file

# test client :
is a very basic service secured by JWT token. Once token is issued, you can test it by adding auth header with JWT token as follows: Authorization  bearer <token>
