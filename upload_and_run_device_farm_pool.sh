#!/usr/bin/env bash

get_arn () {
    data=$1
    arnName=$2
    local arn=""
    counter=0

    while [ -z $arn ]; do
        name=`echo $data | jq ".[$counter].name"`

        [[ $name == "\"$arnName\"" ]] && arn=`echo $data | jq ".[$counter].arn" | sed -e 's/^"//' -e 's/"$//'`
        [[ $name == null ]] && exit 1 # Exits if the pool name is not there in the list

        counter=`expr $counter + 1`
    done

    echo $arn
}

echo "============= GET PROJECT ARN ===================="
projects=`aws devicefarm list-projects | jq '.projects'`
projectArn=`get_arn "$projects" "SDK CI Tests"`

echo "============= GET DEVICE POOL ==================="
devicePools=`aws devicefarm list-device-pools --arn $projectArn | jq .devicePools`
devicePoolArn=`get_arn "$devicePools" "SDK Release Testing"`

echo "============= CREATE UPLOAD ======================"
appUpload=`aws devicefarm create-upload --project-arn $(echo $projectArn) --name app.apk --type ANDROID_APP | jq '.upload'`
testAppUpload=`aws devicefarm create-upload --project-arn $(echo $projectArn) --name app-Test.apk --type INSTRUMENTATION_TEST_PACKAGE | jq '.upload'`

appPreSignedUrl=`echo $appUpload | jq '.url'`
appUploadArn=`echo $appUpload | jq '.arn' | sed -e 's/^"//' -e 's/"$//'`
testAppPreSignedUrl=`echo $testAppUpload | jq '.url'`
testAppUploadArn=`echo $testAppUpload | jq '.arn'  | sed -e 's/^"//' -e 's/"$//'`

echo "============= UPLOAD APK ========================="
curl -T app/build/outputs/apk/release/app-release-unsigned.apk "$(echo $appPreSignedUrl | sed -e 's/^"//' -e 's/"$//')"
curl -T app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk "$(echo $testAppPreSignedUrl | sed -e 's/^"//' -e 's/"$//')"

sleep 60s # Wating for the upload to completely process, if the upload is not processed, schedule run will return the "Missing or unprocessed resources". Upload processing is usually very quick. https://forums.aws.amazon.com/message.jspa?messageID=747302#747302
echo "============= RUN TESTS =========================="
runArn=`aws devicefarm schedule-run --project-arn $projectArn --app-arn $appUploadArn --device-pool-arn $devicePoolArn --name SDK_IMPLEMENTATION_TESTS --test type=INSTRUMENTATION,testPackageArn=$testAppUploadArn | jq '.run.arn'  | sed -e 's/^"//' -e 's/"$//'`

while true; do
    result=`aws devicefarm get-run --arn $runArn | jq '.run'`
    echo "Status: `$result | jq '.status'`"
    if [ `echo $result | jq '.status'` == "\"COMPLETED\"" ]
    then
        echo "Result: `$result | jq '.result'`"
        if [ `echo $result | jq '.result'` != "\"PASSED\"" ]
        then
            echo "============= TESTS HAVE FAILED =============="
            exit 1
        fi
    else
        sleep 120s # Waits for 2mins
    fi
done

echo "============= TESTS HAVE PASSED ================="
exit 0
