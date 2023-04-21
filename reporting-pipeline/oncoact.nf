// Declare syntax version
nextflow.enable.dsl=2

params.orange_path = "$HOME/tmp/orange-output/COLO829v003T.orange.json"
params.oncoact_path = "$HOME/hartwigmedical/oncoact"
params.rose_path = "${params.oncoact_path}/rose"
params.protect_path = "${params.oncoact_path}/protect"

params.patient_id = "pid"
params.primary_tumor_doids = 162

process orange {
    input:
        path input
    output:
        path "orange.json"
    """
    cp ${input} "orange.json"
    """
}

process rose {
    input:
        path 'in/orange.json'
        val patient_id
    output:
        path 'out/*.rose.tsv'
    """
    java -Xmx1G -jar ${params.rose_path}/target/rose-jar-with-dependencies.jar \
    -properties_file ${params.rose_path}/run-local.properties \
    -patient_id ${patient_id}
    """
}

process protect {
    input:
        path 'in/orange.json'
        val primary_tumor_doids
    output:
        path 'out/*.protect.tsv'
    """
    java -Xmx1G -jar ${params.protect_path}/target/protect-jar-with-dependencies.jar \
    -properties_file ${params.protect_path}/run-local.properties \
    -primary_tumor_doids ${primary_tumor_doids}
    """
}

workflow {
    def orange_out = orange(params.orange_path)
    def rose = rose(orange_out, params.patient_id)
    def protect = protect(orange_out, params.primary_tumor_doids)
}