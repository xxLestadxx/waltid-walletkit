# waltid-walletkit

[![CI/CD Workflow for Walt.ID Wallet Kit](https://github.com/walt-id/waltid-walletkit/actions/workflows/ci.yml/badge.svg?branch=master)](https://github.com/walt-id/waltid-walletkit/actions/workflows/ci.yml)

The **walt.id walletkit** provides the API and backend business logic for the walt.id web wallet.
Additionally, it includes a reference implementation of a Verifier and Issuer Portal backend. 

The provided services include:

### Web walletkit
* **User management**
    * Authorization is currently mocked and not production ready
    * User-context switching and user-specific encapsulated data storage
* **Basic user data management**
  * List dids
  * List credentials
* **Verifiable Credential and Presentation exchange**
  * Support for credential presentation exchange based on OIDC-SIOPv2 spec

### Verifier portal backend
* **Wallet configuration**
  * Possibility to configure list of supported wallets (defaults to walt.id web wallet) 
* **Presentation exchange**
  * Support for presentation exchange based on OIDC-SIOPv2 spec

### Issuer portal backend
* **Wallet configuration**
  * Possibility to configure list of supported wallets (defaults to walt.id web wallet)
* **Verifiable credential issuance**
  * Support for issuing verifiable credentials to the web wallet, based on OIDC-SIOPv2 spec


## Related components
* Web wallet frontend https://github.com/walt-id/waltid-web-wallet
* Verifier portal https://github.com/walt-id/waltid-verifier-portal
* Issuer portal https://github.com/walt-id/waltid-issuer-portal

## Test deployment

The snap-shot version of this repository is automatically deployed for testing purpose. Feel free to access the test system at the following endpoints:

* https://issuer.walt.id
* https://wallet.walt.id
* https://verifier.walt.id

## Usage

Configuration and data are kept in sub folders of the data root:
* `config/`
* `data/`

Data root is by default the current **working directory**.

It can be overridden by specifying the **environment variable**: 

`WALTID_DATA_ROOT`

### Verifier portal and wallet configuration:

**config/verifier-config.json**

```
{
  "verifierUiUrl": "http://localhost:4000",                 # URL of verifier portal UI
  "verifierApiUrl": "http://localhost:8080/verifier-api",   # URL of verifier portal API
  "wallets": {                                              # wallet configuration
    "walt.id": {                                            # wallet configuration key
      "id": "walt.id",                                      # wallet ID
      "url": "http://localhost:3000",                       # URL of wallet UI
      "presentPath": "CredentialRequest",                   # URL subpath for a credential presentation request
      "receivePath" : "ReceiveCredential/",                 # URL subpath for a credential issuance request
      "description": "walt.id web wallet"                   # Wallet description
    }
  }
}
```

### Issuer portal and wallet configuration:

**config/issuer-config.json**

```
{
  "issuerUiUrl": "http://localhost:5000",                   # URL of issuer portal UI
  "issuerApiUrl": "http://localhost:8080/issuer-api",       # URL of issuer portal API (needs to be accessible from the walletkit)
  "wallets": {                                              # wallet configuration
    "walt.id": {                                            # wallet configuration key
      "id": "walt.id",                                      # wallet ID
      "url": "http://localhost:3000",                       # URL of wallet UI
      "presentPath": "CredentialRequest",                   # URL subpath for a credential presentation request
      "receivePath" : "ReceiveCredential/",                 # URL subpath for a credential issuance request
      "description": "walt.id web wallet"                   # Wallet description
    }
  }
}
```

### Wallet backend configuration

User data (dids, keys, credentials) are currently stored under

`data/<user@email.com>`

It is planned to allow users to define their own storage preferences, in the future.

### APIs

The APIs are launched on port 8080.

A **swagger documentation** is available under 

`/api/swagger`

**Wallet API** is available under the context path `/api/`

**Verifier portal API** is available under the context path `/verifier-api/`

**Issuer portal API** is available under the context path `/issuer-api/`

## Build & run the Web Wallet Kit

_Gradle_ or _Docker_ can be used to build this project independently. Once running, one can access the Swagger API at http://localhost:8080/api/swagger

### Gradle

    gradle build

unzip package under build/distributions and switch into the new folder. Copy config-files _service-matrix.properties_ and _signatory.conf_ from the root folder and run the bash-script:

    ./bin/waltid-walletkit
    
To run the backend you will execute:
   ```waltid-walletkit run```
To have issuers, you will have to execute: 
   ```waltid-walletkit --init-issuer```

### Docker

    docker build -t waltid/walletkit .

    docker run -it -p 8080:8080 waltid/walletkit

## Running all components with Docker Compose

To spawn the **backend** together with the **wallet frontend**, the **issuer-** and the **verifier-portal**, one can make use of the docker-compose configuration located in folder:

`./docker/`

In order to simply run everything, enter:

    docker-compose up

This configuration will publish the following endpoints by default:
* **web wallet** on _**[HOSTNAME]:8080**_
  * wallet frontend: http://[HOSTNAME]:8080/
  * wallet API: http://[HOSTNAME]:8080/api/
* **verifier portal** on _**[HOSTNAME]:8081**_
  * verifier frontend: http://[HOSTNAME]:8081/
  * verifier API: http://[HOSTNAME]:8081/verifier-api/
* **issuer portal** on _**[HOSTNAME]:8082**_
  * issuer frontend: http://[HOSTNAME]:8082/
  * issuer API: http://[HOSTNAME]:8082/issuer-api/

*Note*

**[HOSTNAME]** is your local computer name. Using **localhost**, not all features will work correctly.

Visit the `./docker`. folder for adjusting the system config in the following files
* **docker-compose.yaml** - Docker config for launching containers, volumes & networking
* **ingress.conf** - Routing config
* **config/verifier-config.json** - verifier portal configuration
* **config/issuer-config.json** - issuer portal configuration

## Initializing Wallet Kit as EBSI/ESSIF Issuer

By specifying the optional startup parameter **--init-issuer** the walletkit can be initialized as issuer-backend in line with the EBSI/ESSIF ecosystem. Note that this is for demo-purpose only.

```
cd docker
docker pull waltid/walletkit
docker run -it -v $PWD:/waltid-walletkit/data-root -e WALTID_DATA_ROOT=./data-root waltid/walletkit --init-issuer

# For the DID-method enter: "ebsi"
# For the bearer token copy/paste the value from: https://app.preprod.ebsi.eu/users-onboarding
```

The initialization routine will output the DID, which it registered on the EBSI/ESSIF ecosystem.


## Relevant Standards

- Self-Issued OpenID Provider v2 https://openid.bitbucket.io/connect/openid-connect-self-issued-v2-1_0.html
- OpenID Connect for Verifiable Presentations https://openid.net/specs/openid-connect-4-verifiable-presentations-1_0-07.html
- OpenID Connect for Verifiable Credential Issuance https://tlodderstedt.github.io/openid-connect-4-verifiable-credential-issuance-1_0-01.html
- EBSI Wallet Conformance https://ec.europa.eu/digital-building-blocks/wikis/display/EBSIDOC/EBSI+Wallet+Conformance+Testing
- Verifiable Credentials Data Model 1.0 https://www.w3.org/TR/vc-data-model/
- Decentralized Identifiers (DIDs) v1.0 https://w3c.github.io/did-core/
- DID Method Rubric https://w3c.github.io/did-rubric/
- did:web Decentralized Identifier Method Specification https://w3c-ccg.github.io/did-method-web/
- The did:key Method v0.7 https://w3c-ccg.github.io/did-method-key/


## License

```
Copyright ((C)) 2022 walt.id GmbH

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
