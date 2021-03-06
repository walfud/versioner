# Usage

##### Increase build number (Usually by CI)
```shell
curl -X POST localhost:8080/version -H "Content-Type: application/json" -d '{"id": "com.walfud.versioner", "current": "1.2.3", "part": "build", "ret": "major.minor.patch.build-3"}'

# or
curl -X POST localhost:8080/version -H "Content-Type: application/json" -d '{"id": "com.walfud.versioner", "current": "1.2.3.0", "part": "build", "ret": "major.minor.patch.build-3"}'
```
> "1.2.3.001" or "1.2.3.002" or until a build number not taken up by others

##### Increase patch number
```shell
curl -X POST localhost:8080/version -H "Content-Type: application/json" -d '{"id": "com.walfud.versioner", "current": "1.2.3", "part": "patch", "ret": "major.minor.patch"}'
```
> "1.2.3" or "1.2.4" or until a patch number not taken up by others

### Parameter

##### id
One `id` per application. It's usually be your package name.

##### current
Current version

##### part
If the version has taken by others, which part should be increased.
 
Can only be one of _major_ or _minor_ or _patch_ or _build_.

##### ret
Return value your want.

Can be composition of _major_ or _minor_ or _patch_ or _build_, joined by _._(dot character).

Each part can be fixed length by "_part_-N".

Common usage:

| format | description | return | 
| :--- | :--- | :--- |
| _major.minor.patch_ | return sematic version | 1.2.3 |
| _major.minor.patch.build_ | return sematic version with build number | 1.2.3.4 | 
| _major.minor.patch.build-3_ | build part with fixed length | 1.2.3.004 |

##### API docs
OpenAPI: [http://localhost:8080/swagger-ui/index.html?configUrl=%2Fv3%2Fapi-docs%2Fswagger-config](http://localhost:8080/swagger-ui/index.html?configUrl=%2Fv3%2Fapi-docs%2Fswagger-config)


# Rule
* Minimum build number is 1, other is 0. That means the minimum _major.minor.patch_ is "0.0.0", while _major.minor.patch.build_ is "0.0.0.1". 
* Return new version or your version? <br />
  a. If the version does **NOT EXIST**, your expected version is return. <br />
  b. If the version does **EXIST**, a new version with increased part is return.


# Install & Deploy
**_docker_ and _docker-compose_ is required**.

```shell
git clone https://github.com/walfud/versioner.git
cd ./versioner
docker-compose up -d
```

server listening on _10000_, mysql listening on _10010_ and _10011_(debug)


# TODO
* actuator & doc
