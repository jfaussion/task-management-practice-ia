sudo apt-get install -y wget apt-transport-https gpg \ 
&& wget -qO - https://packages.adoptium.net/artifactory/api/gpg/key/public | gpg --dearmor | sudo tee /etc/apt/trusted.gpg.d/adoptium.gpg > /dev/null \
&& echo "deb https://packages.adoptium.net/artifactory/deb $(awk -F= '/^VERSION_CODENAME/{print$2}' /etc/os-release) main" | sudo tee /etc/apt/sources.list.d/adoptium.list \
&& sudo apt-get update \
&& sudo apt-get install -y temurin-21-jdk \
&& echo '
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
' >> ~/.profile && source ~/.profile

wget https://dlcdn.apache.org/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.tar.gz \
&& tar -xvf apache-maven-3.9.9-bin.tar.gz \
&& sudo mv apache-maven-3.9.9 /opt/ \
&& sudo ln -s /opt/apache-maven-3.9.9/ /opt/maven \
&& rm apache-maven-3.9.9-bin.tar.gz \
&& echo '
export JAVA_HOME=/usr/lib/jvm/temurin-21-jdk-amd64
export M2_HOME=/opt/maven
export MAVEN_HOME=/opt/maven
export PATH=${M2_HOME}/bin:${PATH}
' >> ~/.profile \
&& source ~/.profile
