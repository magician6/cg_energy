pipeline {
    agent {
            node {
                label 'dev_linux'

            }
     }
    environment {
           // 镜像仓库名
           IMAGE_REPO="docker-devops.wilmar.cn/freightestimate"
           // 提交到 git 时的用户名和密码
           BUILD_USER = 'jenkins'
           BUILD_USER_EMAIL = 'jenkins@Argo CD.com'
           // 发布的配置文件的 GIT REPO  及GIT 库的凭证ID
           GIT_CREDENTIAL_ID='yangdazhi'
           YAML_REPO_URL='https://wilmar-intl@dev.azure.com/wilmar-intl/%E7%B1%B3%E4%B8%9A%E8%BF%90%E8%B4%B9%E6%8A%A5%E4%BB%B7%E5%B9%B3%E5%8F%B0/_git/wilmar-K8s-deployment'
           KEY_WORD='Code makes me happy'
           SKIPBUILD="false"
    }
 
    triggers {
        // 每分钟
        pollSCM('H/2 * * * *')
    }
    stages {

        // 检查提交的 mark 中是否启有指定的魔术值
        stage("check Magic Value"){
              environment {
                COMMIT_ID = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
                MESSAGE= sh(returnStdout: true, script: "git log --pretty=format:“%s” ${COMMIT_ID} -1").trim()
              }
            steps{
                    sh """
                    if echo "$MESSAGE" | grep -q  '${KEY_WORD}'
                    then
                        echo "开始构建..."
                        exit 0
                    else
                        echo "没有魔法值，退出...."
                    	exit 1
                    fi
                    """
            }
            post{
                failure {
                    script{
                        SKIPBUILD="true"
                        sh"exit 0"
                    }
                }
            }
        }

        // 编译 JAVA 代码
        stage('Build image') {
            steps {
                sh 'id'
               // 注意在分支合并时不要合并本行
                sh 'mvn -B -DskipTests clean package'
            }
        }

        // 上传 image
        stage('Push image'){

            steps{
                sh 'mvn dockerfile:push'
            }
        }


        // 清理image
        stage('Clean Image') {
            steps {
                sh "docker image prune -f"
            }
        }



//          // 修改发布的 git repository
//          // uat,sit,prd 都修改git repository,只是prd的cd 手动触发
//          // 更改demo-config库下demo/demo.yaml文件内镜像tag
//          // 提交更改至demo-config库
//         stage('update docker tag') {
//              environment {
//                  // 获取的是代码的提交Id
//                  COMMIT_ID = sh(returnStdout: true, script: "git log -n 1 --pretty=format:'%h'").trim()
//                  // 取得当前时间
//                  TIMESTAMP = sh(returnStdout: true, script: 'date "+%Y-%m-%d %H:%M:%S"').trim()
//                  // 取得 将要更改的 yaml 文件名
//                  FILE_NAME = sh(returnStdout:true, script:"echo deployment-`[[ ${BRANCH_NAME} == master ]] && echo sit || echo ${BRANCH_NAME}`").trim()
//                  // 读取image的TAG 从 target>docker>tag 文件中读取
//                  DOCKER_TAG=sh(returnStdout:true, script:"cat target/docker/tag").trim()
//                  MESSAGE= sh(returnStdout: true, script: "git log --pretty=format:“%s” ${COMMIT_ID} -1").trim()
//              }
//
//              steps {
//                  withCredentials([gitUsernamePassword(credentialsId: 'wy_yangdazhi', gitToolName: 'Default')]) {
//                     sh 'id'
//                     // 如果不存在则 CLONE 一个
//                     sh """
//                      if [ ! -d "wilmar-K8s-deployment" ];then
//                         git clone ${YAML_REPO_URL}
//                      fi
//                     """
//                     dir('wilmar-K8s-deployment'){
//                                             sh """
//                                              git fetch --all &&  git reset --hard origin/main && git pull
//                                              pwd
//                                              echo ${IMAGE_REPO}
//                                              echo ${COMMIT_ID}
//                                              echo ${FILE_NAME}
//                                              echo ${DOCKER_TAG}
//
//
//                                             git config --global user.name "yangdazhi"
//                                             git config --global user.email "yangdazhi@cn.wilmar-intl.com"
//                                             git config --global push.default simple
//
//
//                                              sed -i "s#docker-devops.wilmar.cn/freightestimate.*#docker-devops.wilmar.cn/freightestimate:${DOCKER_TAG}#g" 30670-miye/yfbj/deployment-sit.yaml
//                                              sed -i "s#buildTime.*#buildTime:  ${TIMESTAMP}#g" 30670-miye/yfbj/deployment-sit.yaml
//
//                                              git add -A && git commit -m "[ jenkins自动更新 ] e tag: [${DOCKER_TAG}]  msg:${MESSAGE}" && git push origin main
//                                             """
//                                          }
//                  }
//              }
//         }


    }
}