# Floor Demo - Pi writes a file, Spring reads the file, Spring writes the file in CSV format

### Pi Setup
```
Raspberry Pi4
	•	If not already done, install NOOBS OS on your Pi. It’s on the microSD card in a box with Pi. 
	•	Connect keyboard to Pi
	•	Connect mouse to Pi
	•	Connect LCD with HDMI cable to Pi
	•	Place microSD card with NOOBS in micro SD port in Pi
	•	Start Pi
	•	Follow installation instructions
	•	Enabling ssh on Raspberry Pi and set Host to pf4devicesdev1.local.
	•	sudo apt-get update
	•	sudo apt-get install avahi-utils
	•	avahi-resolve-address 10.0.0.125
	•	Check if pf4devicesdev1.local is accessible with ping
	•	Check what power plan has wlan interface with:
	•	sudo iw wlan0 get power_save
	•	if it’s enabled, then disable it with:
	•	sudo iw wlan0 set power_save off
	•	Install docker (based on documentation from: https://howchoo.com/g/nmrlzmq1ymn/how-to-install-docker-on-your-raspberry-pi):
	•	https://docs.docker.com/engine/install/ubuntu/
	•	https://www.docker.com/blog/changes-dockerproject-org-apt-yum-repositories/
	•	Install certificates:
	•	sudo apt-get install apt-transport-https ca-certificates software-properties-common
	•	Add docker’s GPG key:
	•	curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
	•	Check the key:
	•	sudo apt-key fingerprint 0EBFCD88
	•	And now – it differs from description from howchoo page – install docker from the script:
	•	curl -sSL https://get.docker.com | sh
	•	sudo groupadd docker
	•	sudo gpasswd -a pi docker
	•	logout
	•	login
	•	sudo apt-get install build-essential libssl-dev libffi-dev python-dev
	•	sudo apt-get install -y python python-pip
	•	 sudo pip3 -v install docker-compose
	•	docker-compose —version
	•	check java version with command:
	•	java -version
	•	sudo apt-get install openjdk-8-jdk
	•	sudo update-alternatives --config javac (select 2 - jdk8)
	•	sudo update-alternatives --config java (select 2 - sjre8)
	•	java -versions
```

#### Test Board
```
https://medium.com/youngwonks/raspberry-pi-the-must-have-stem-tool-for-children-861341f8647e
create led.py
python3 led.py

Should see led turn on, turn off, repeat.
NOTE - use Python for low level discrete programming.  The Pi works well with Python libraries for this, not so much other languages.
```

#### Test App

From host:

```
cd java-spring-rest-pi
mvn clean install
```

```
git init
git add .
git commit -m "pi"
git push -u origin master
```

```
ssh pi@pf4devicedev1.local
```

From pi:

```
sudo su
cd
git pull

mvn clean install
java -jar target/pi.jar
```


### Test (file polling every 1min)

Pi

```
http://pf4devicesdev1.local:9094/sensors/list
http://pf4devicesdev1.local:9094/alerts/list/CsvAlert
http://pf4devicesdev1.local:9094/alerts/setOn/CsvAlert
http://pf4devicesdev1.local:9094/alerts/setOff/CsvAlert
```

### What does the Pi do - Reads the room temperature
By installing a temp sensor and activating it:

```
modprobe w1-gpio  
modprobe w1-therm
```

The Pi automagically generates a file with the room temperature in the following format:

```
4d 01 4b 46 7f ff 03 10 d8 : crc=d8 YES  
4d 01 4b 46 7f ff 03 10 d8 t=20812  
```

The Celsius temperature is the last bit 20812.
	
	- divide by 1000 and you have your Celsius temperature of 20.8.
	
	- tempF = ((tempC * (9 / 5.0f)) + 32);
	
### Stuff left todo

Eureka for service discovery

Zuul for api gateway

spring-database micro for saving data

spring-x micro for other stuff

k8s orchestration