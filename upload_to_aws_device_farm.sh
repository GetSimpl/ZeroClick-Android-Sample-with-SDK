#!/usr/bin/env bash

echo "=============GET PROJECT ARN===================="
projectArn=`aws devicefarm list-projects | jq '.projects[0].arn' | sed -e 's/^"//' -e 's/"$//'`

echo "=============CREATE UPLOAD======================"
preSignedUrl=`aws devicefarm create-upload --project-arn $(echo $projectArn) --name app-debug.apk --type ANDROID_APP | jq '.upload.url'`

echo "=============UPLOAD APK========================="
curl -T app/build/outputs/apk/debug/app-debug.apk "$(echo $preSignedUrl | sed -e 's/^"//' -e 's/"$//')"