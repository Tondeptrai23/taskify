def deployWithHelm(String service, String namespace, String tag, String valuesFile = null) {
    echo "Deploying ${service} to Kubernetes using Helm..."
    
    def helmCommand = "helm upgrade --install ${service} ./helm/taskify/charts/${service} " +
                     "--set image.tag=${tag} " +
                     "--namespace=${namespace} "
                     
    if (valuesFile) {
        helmCommand += "--values=${valuesFile} "
    }
    
    sh helmCommand
    
    echo "${service} deployed successfully to Kubernetes namespace: ${namespace}"
}

return this