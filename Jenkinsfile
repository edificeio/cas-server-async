pipeline {
  agent any
  stages {
    stage('Build') {
      steps {
        sh 'mvn clean package'
      }
    }
    stage('Publish') {
      steps {
        sh '''
          export jarFile=`ls target/cas-server-async-*.jar | grep -v javadoc | grep -v sources`
          export jarVersion=`echo $jarFile | sed 's|target/cas-server-async-||' | sed 's/.jar//'`
        case "$jarVersion" in
          *SNAPSHOT) export nexusRepository='snapshots' ;;
          *)         export nexusRepository='releases' ;;
          esac
            mvn deploy:deploy-file -DgroupId=fr.wseduc -DartifactId=cas-server-async -Dversion=$jarVersion -Dpackaging=jar -Dfile=$jarFile -DrepositoryId=ode-$nexusRepository -Durl=https://maven.opendigitaleducation.com/nexus/content/repositories/$nexusRepository/
        '''
      }
    }
  }
}

