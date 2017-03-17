# Twitter Search application - api gateway

To be used with https://github.com/shafiquejamal/paytmlsesjwebfrontend as the web front end

## Requirements

- Java 1.8+
- SBT

## How to run locally (Mac or Linux)

1. In a terminal tab, clone the repository into a directory and cd into that directory:
```
git https://github.com/shafiquejamal/paytmlsesjapigateway.git /path/to/project
cd /path/to/project
```
2. Set any environment variables as indicated in `application.conf`, for example (fill in the missing values, replace the others as necessary):
```
export PAYTMSESJAPIGATEWAY_PLAY_CRYPTO_SECRET=SOME_SECRET_CHARS
export PAYTMSESJAPIGATEWAY_DEFAULT_DB_DRIVER=jdbc:postgresql://localhost:5432/paytmlsesjapigateway
export PAYTMSESJAPIGATEWAY_DEFAULT_DB_USERNAME=postgres
export PAYTMSESJAPIGATEWAY_DEFAULT_DB_PASSWORD=postgres
export PAYTMSESJAPIGATEWAY_TEST_DB_DRIVER=jdbc:postgresql://localhost:5432/paytmlsesjapigatewaytest
export PAYTMSESJAPIGATEWAY_TEST_DB_USERNAME=postgres
export PAYTMSESJAPIGATEWAY_TEST_DB_PASSWORD=postgres
export PAYTMSESJAPIGATEWAY_MD5_ACTIVATION_KEY=some_secret_md5_activation_key
export PAYTMSESJAPIGATEWAY_REQUIRE_EMAIL_ACTIVATION=true
export PAYTMSESJAPIGATEWAY_EMAILER_EMAIL_FROM='Postmaster <postmaster@yourdomain.com>'
export PAYTMSESJAPIGATEWAY_TEST_EMAIL_RECIPIENT='Your Name <your.name@yourdomain.com>'
export PAYTMSESJAPIGATEWAY_JWT_VALIDITY_DAYS=-1
export PAYTMSESJAPIGATEWAY_X=13c1db19f37ff7c046d1ea35a9a553f5377e4d58a8998f743e5d157bfaf28ef6ba76ecb3468b088b973258c122414b9663de0022a49f1ce690695139c5311373689
export PAYTMSESJAPIGATEWAY_Y=171acb62b8ddc51d4af28f105ef08c8dcc29be07a627cd2e7b9ee60219ef6f1fdbe6f3b73ed1f7239e722036adaed28d4ca3c79ef7cb0c1fa7cbbf891b66dea9ad6
export PAYTMSESJAPIGATEWAY_S=17d1f79145726fc7ecd417400c06fc414f06ccffd4c9f7e0480186dbca8aaf88e261b5930dc4c7322525b059972b8bd68ee2c73cf23a1c14c5b6d21a7a25595e4ef
export PAYTMSESJAPIGATEWAY_EMAILER_HOST=smtp.mailgun.org
export PAYTMSESJAPIGATEWAY_EMAILER_PORT=587
export PAYTMSESJAPIGATEWAY_EMAILER_TLS=yes
export PAYTMSESJAPIGATEWAY_EMAILER_USER=postmaster@yourdomain.com
export PAYTMSESJAPIGATEWAY_EMAILER_PASSWORD=yoursmtppassword
export PAYTMSESJAPIGATEWAY_EMAILER_MOCK=false
export PAYTMSESJAPIGATEWAY_TWITTER_CONSUMER_KEY=
export PAYTMSESJAPIGATEWAY_TWITTER_CONSUMER_SECRET=
export PAYTMSESJAPIGATEWAY_TWITTER_TOKEN=
export PAYTMSESJAPIGATEWAY_TWITTER_TOKEN_SECRET=
```

To generate your own public and private key (the X, Y and S values above), see: http://pauldijou.fr/jwt-scala/samples/jwt-ecdsa/

Here is how I did it: In `SBT`, type `console` to get to the Scala console for the project, and run the following commands:

```
import java.security.Security
import java.security.spec.{ECPrivateKeySpec, ECPublicKeySpec, ECGenParameterSpec, ECParameterSpec, ECPoint}
import java.security.{SecureRandom, KeyFactory, KeyPairGenerator}

Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider())
val generatorEC = KeyPairGenerator.getInstance("ECDSA", "BC")
val ecGenSpec = new ECGenParameterSpec("P-521")
generatorEC.initialize(ecGenSpec, new SecureRandom())
val ecKey = generatorEC.generateKeyPair()

ecKey.getPrivate
ecKey.getPublic
```

Then exit the Scala console.

3. Run `SBT`:
```
sbt
```
and wait for the dependencies to be downloaded

4. In SBT run the following commands:
```
;clean ;test; ~run 9001
```

5. The api-gateway should now be ready for use with the mobile or web front end (web: https://github.com/shafiquejamal/paytmlsesjwebfrontend). 