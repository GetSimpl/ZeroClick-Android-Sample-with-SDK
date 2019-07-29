#!/usr/bin/env bash

echo "=============GET PROJECT ARN===================="
projectArn=`aws devicefarm list-projects | jq '.projects[0].arn' | sed -e 's/^"//' -e 's/"$//'`

echo "=============CREATE UPLOAD======================"
appPreSignedUrl=`aws devicefarm create-upload --project-arn $(echo $projectArn) --name app-debug.apk --type ANDROID_APP | jq '.upload.url'`
testAppPreSignedUrl=`aws devicefarm create-upload --project-arn $(echo $projectArn) --name app-debug.apk --type INSTRUMENTATION_TEST_PACKAGE | jq '.upload.url'`

echo "=============UPLOAD APK========================="
curl -T app/build/outputs/apk/debug/app-debug.apk "$(echo $preSignedUrl | sed -e 's/^"//' -e 's/"$//')"
curl -T app/build/outputs/apk/androidTest/debug/app-debug-androidTest.apk
