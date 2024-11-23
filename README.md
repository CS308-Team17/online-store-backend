# cs308-online-store-backend

### Branching

Newly opened branches should be named as follows:

```sh
If its a feature branch : "feature/<author name letters>/<feature name>-#<issue number>"
If its a bugfix branch : "bugfix/<author name letters>/<bugfix name>-#<issue number>"

Example: feature/mk/add-home-view-#1
Example: bugfix/mk/fix-home-view-localization-#1
```
User login and token-based authentication.
Dynamic secret key configuration via environment variables.

Set the JWT secret key:

Linux/Mac: export JWT_SECRET_KEY="your-secret-key"
Windows: set JWT_SECRET_KEY="your-secret-key"
Run the project: mvn spring-boot:run.

api ex:
{ "username": "user", "password": "password" }
Add the token to the Authorization header:
Authorization: Bearer your.jwt.token

