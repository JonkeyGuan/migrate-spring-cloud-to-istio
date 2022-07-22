# migrate-spring-cloud-to-istio

## Test on local
```
curl -X POST -H "Content-Type: application/json" http://localhost:8083/application -d @cardApplication.json
```

## Test on OpenShift
### Suppose:
* Openshift Base Domain: apps.ocp1.example.com
* Applicaion Project Name: springcloud-migration

#### If you have a diffent domain, please run below to replace them all
```
grep -rl 'ocp' . | grep -E '.yaml|.md' | xargs sed -i "" 's/ocp1.example.com/ocp.xxx.com/g'
```

### Test

```
curl -X POST -H "Content-Type: application/json" http://card.sm.apps.ocp1.example.com/application -d @cardApplication.json

siege -c 4  -r 20 -v -T "application/json" "http://card.sm.apps.ocp1.example.com/application POST < cardApplication.json"
```
