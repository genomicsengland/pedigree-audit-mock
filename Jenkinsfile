pipeline {
    agent {
        label 'maven'
    }
    environment {
        BOOTSTRAP_SERVERS = 'localhost:9092'
        POSTGRES_URL = credentials('PNP_POST_URL')
        ARTIFACTORY_URL = "${env.EXTERNAL_DOCKER_REGISTRY}"
        OSE_PROJ = credentials('OSE_PROJ')
        POSTGRES_PASS = credentials('PED_POSTGRES_PASS')
        GITHUB_CREDS = credentials('jstanley1_creds')
        GITHUB_PASS64 = credentials("GITHUB_ENCODED_PASSWORD")
        PUSH_SECRET = credentials('PUSH_SECRET')
        scmVars = checkout scm
        gitBranch = sh(
                script: "echo ${scmVars.GIT_BRANCH} | cut -d '/' -f2",
                returnStdout: true).trim()
    }

    stages {
        stage('Preflight Checklist') {
            when {
                allOf {
                    expression { env.POSTGRES_URL != null }
                    expression { env.BOOTSTRAP_SERVERS != null }
                    expression { env.ARTIFACTORY_URL != null }
                    expression { env.OSE_PROJ != null }
                    expression { env.POSTGRES_PASS != null }
                }
            }
			
			steps {
				timestamps {
					logstash {
						echo 'Preflight Passed. Prepare to board.'
					}
				}
			}
        }
        
        stage('Build jar file') {
            // Run the maven build
			
			steps {
				timestamps {
					logstash {		
						sh "pwd"
						script {
							sh 'mvn -Dmaven.test.skip=true package'

							VERSION = sh (
									script : '''mvn -q -Dexec.executable=echo -Dexec.args='${project.version}' \\
												--non-recursive exec:exec 2>/dev/null''',
									returnStdout: true
							).trim()
						}
					}
				}
			}
        }
        stage('Run Unit Tests') {
           	steps {
				timestamps {
					logstash {
						script {
								sh '''mvn test jacoco:report -Dspring.profiles.active=test \\
										-Dsurefire.suiteXmlFiles=src/test/resources/unit-tests.xml'''
								sh "mv target/site/jacoco/index.html unit.html"
								sh "mv target/surefire-reports/TestSuite.txt unit.txt"

								sh '''mvn test jacoco:report -Dspring.profiles.active=test \\
										-Dsurefire.suiteXmlFiles=src/test/resources/integration-tests.xml'''
								sh "mv target/surefire-reports/TestSuite.txt integration.txt"

						}
					}
				}
			}
        }

        stage('Build and Push Docker Image (develop)') {
            steps {
				timestamps {
					logstash {
						script {
							try {
								sh 'set +x'
								sh 'oc project $OSE_PROJ'
								sh """oc new-build --binary=true --name=pedigree-mock --to-docker=true \\
											--to=\"$ARTIFACTORY_URL/pedigree-mock:$VERSION\" \\
											--push-secret=\"$PUSH_SECRET\""""

								// Execute the build config to build the Docker image and push to Artifactory
								sh "oc start-build pedigree-mock --from-dir=. --follow=true --wait"
							} catch (err) {
								echo "Error caught in step. Deleting build config in Open Shift."
								echo "Caught: ${err}"
								currentBuild.result = 'FAILURE'
							} finally {
								// Delete the created build config to keep OSE clean for demo purposes
								// Note: If the build config is kept, then "oc new-build" is not needed for subsequent builds.
								sh 'oc delete bc/pedigree-mock'
							}
						}
					}
				}
			}
        }
        stage('Deploy to Build Cluster') {
            steps {	
				timestamps {
					logstash {
						script {
							sh 'set +x'
							sh "oc import-image --confirm pedigree-mock:$VERSION --from=\"$ARTIFACTORY_URL/pedigree-mock:$VERSION\""
						}
					}
				}
            }
        }
    }
}
