# PairTreeOverHTTP
Serve a PairTree (https://wiki.ucop.edu/display/Curation/PairTree) over HTTP

# How To Use

## Deployment

Built the distribution using gradle:

```
$ ./gradlew :distZip
```

Copy the distribution from ```$SRC_DIR/build/distributions``` to your preffered location. Finally, start by specifying pairtree root and service port:

```
./$PAIRTREE_OVER_HTTP/bin/PairTreeOverHTTP -r $PAIRTREE_ROOT -p 9090
```

## Sample Request

```
curl -H "Content-Type: application/json" -X POST -d '{"volumes": ["miua.5252888,0001,001","miua.5252814,0001,001","miua.5252798,0004,002"], "clean":true}' -O http://localhost:8080/volumes
```
