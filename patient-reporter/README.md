

## Building Patient Reporter
Build a docker image for protect:
```shell
docker build . -t patient-reporter:latest
```

Copy the orange JSON input to the input volume.
```shell
docker container create --name temp -v patient-reporter-input:/input busybox

docker rm temp
```

Run protect with the expected arguments.
```shell
docker run --rm \
--name patient-reporter \
--mount source=patient-reporter-output,target=/out \
--mount source=rose-output,target=/rose \
--mount source=protect-output,target=/protect \
--mount source=orange-output,target=/orange \
protect:latest \
-tumor_sample_id 123456 \
-tumor_sample_barcode 654321 \
-output_dir_report /out \
-output_dir_data /out \
-known_fusion_file /data/resources/public/fusions/37/known_fusion_data.37.csv \
-rva_logo 
```