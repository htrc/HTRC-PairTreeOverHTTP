# PairTreeOverHTTP
Serve a PairTree (https://wiki.ucop.edu/display/Curation/PairTree) over HTTP

# How To Use

```
curl -H "Content-Type: application/json" -X POST -d '{"volumes": ["miua.5252888,0001,001","miua.5252814,0001,001","miua.5252798,0004,002"], "clean":true}' -O http://localhost:8080/volumes
```
