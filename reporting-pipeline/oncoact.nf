// Declare syntax version
nextflow.enable.dsl=2

params.patient_id = "pid"
params.primary_tumor_doids = 162
params.orange_path = "$HOME/tmp/orange-output/COLO829v003T.orange.json"

process rose {
    container 'eu.gcr.io/hmf-pipeline-prod-e45b00f2/rose:latest'

    input:
        path 'in/orange.json'
        val patient_id
    output:
        path 'out/*.rose.tsv'
    """
    java -Xmx1G -jar /opt/app/rose.jar -properties_file /opt/app/rose.properties -patient_id ${patient_id}
    """
}

process protect {
    container 'eu.gcr.io/hmf-pipeline-prod-e45b00f2/protect:latest'

    input:
        path 'in/orange.json'
        val primary_tumor_doids
    output:
        path 'out/*.protect.tsv'
    """
    java -Xmx1G -jar /opt/app/protect.jar -properties_file /opt/app/protect.properties -primary_tumor_doids ${primary_tumor_doids}
    """
}

workflow {
    def rose = rose(params.orange_path, params.patient_id)
    def protect = protect(params.orange_path, params.primary_tumor_doids)
}