import java.lang.*;
import java.util.Scanner;
import java.io.*;
import java.util.Formatter;
class Cpp{
	public static final String RED="\u001B[31m",GRN="\u001B[32m",BLU="\u001B[34m",RST="\u001B[0m",WHT="\u001B[37m";
	public static final String BRED="\u001B[41m",BGRN="\u001B[42m",BBLU="\u001B[44m",BRST="\u001B[40m";
	public static final String PWD=System.getProperty("user.dir");
	public String critserv(String service){
		String pftpd="apt-get autoremove  proftpd-basic\n",vftpd="apt-get autoremove vsftpd\n",smb="apt-get autoremove samba\n",ssh="service ssh stop\napt-get -purge remove openssh-server\n",apch="service apache2 stop\napt-get autoremove apache2 apache2-utils apache2.2-bin apache2-common\napt-get autoremove\nwhereis apache2\nrm -rf /etc/apache2\n",msql="apt-get remove mysql-server mysql-client mysql-common -y\napt-get autoremove -y\nrm -rf /etc/mysql\nfind / -iname 'mysql*' -exec rm -rf {} \\;\n";
		if(service.contains("proftpd")){service=vftpd+smb+ssh+apch+msql;}
		else if(service.contains("vsftpd")){service=pftpd+smb+ssh+apch+msql;}
		else if(service.contains("samba")){service=pftpd+vftpd+ssh+apch+msql;}
		else if(service.contains("ssh")){service=pftpd+vftpd+smb+apch+msql;}
		else if(service.contains("apache")){service=pftpd+vftpd+smb+ssh+msql;}
		else if(service.contains("sql")){service=pftpd+vftpd+smb+apch+ssh;}
		return service;
	}
	public String perms(String file){
		String red="",name="";
		try{
			Scanner s=new Scanner(new File(PWD+file));
			while(s.hasNextLine()){
				name=s.nextLine();
				red+="deluser "+name+" sudo\n";
			}
		}catch(Exception e){System.out.println("Error");}
		return red;
	}
	public String passwd(String file){
		String add="",name="";
		try{
			Scanner s=new Scanner(new File(PWD+file));
			while(s.hasNextLine()){
				name=s.nextLine();
				//user stuff
				add+="echo -e \"Password1!\\nPassword1!\" | passwd "+name+"\nusermod -s /bin/bash "+name+"\n";
				System.out.println(GRN+"user "+name+", password changed to \"Password1!\""+RST);
				//crontab
				add+="crontab -u "+name+" -r\n";
			}
			s.close();
		}catch(Exception e){System.out.println("File not found");}
		return add;
	}
	public void printw(StringBuilder output,String file){
		try{
			Scanner s=new Scanner(new File(PWD+file));
			Formatter f=new Formatter(PWD+file);
			while(s.hasNext()){f.format("");}
			FileWriter printx=new FileWriter(PWD+file);
			printx.write(output.toString());
			printx.close();
			s.close();
			f.close();
		}catch(Exception e){System.out.println("File not found");}
	}
	public static void main(String[] args){
		Cpp shell=new Cpp();
		Scanner scan=new Scanner(System.in);
		StringBuilder shl=new StringBuilder();
		shl.append("#!/bin/bash\n");
		//update
		shl.append("mv "+PWD+"sources.list /etc/apt/sources.list\n");
		shl.append("apt-get install unattended-upgrades -y\n");
		shl.append("rm /etc/apt/apt.conf.d/20auto-upgrades\n");
		shl.append("mv "+PWD+"20auto-upgrades /etc/apt/apt.conf.d/\n");
		shl.append("rm /etc/update-manager/release-upgrades\n");
		shl.append("mv "+PWD+"release-upgrades /etc/update-manager/\n");
		shl.append("rm /etc/apt/apt.conf.d/50unattended-upgrades\n");
		shl.append("mv "+PWD+"50unattended-upgrades /etc/apt/apt.conf.d/\n");
		shl.append("apt-get update\n");
		shl.append("unattended-upgrades --dry-run -debug\n");
		shl.append("update-manager -c\n");
		//firefox updates
		shl.append("update-alternatives --config x-www-browser\napt install firefox\n");
		//disable root
		shl.append("passwd -l root\nusermod -p '!' root\n");
		//ufw
		shl.append("apt-get install ufw\napt-get install gufw\n");
		shl.append("ufw default deny incoming\ngufw default deny incoming\n");
		shl.append("ufw default allow outgoing\ngufw default allow outgoing\n");
		System.out.print(BGRN+"Are you using SSH? [y/n]: ");
		System.out.print(RST);
		char chr=scan.next().toLowerCase().charAt(0);
		if(chr=='y'){shl.append("ufw allow 22/tcp\nufw allow 22/udp\ngurw allow 22/tcp\ngufw allow 22/udp\n");}
		else{shl.append("ufw deny 22/tcp\nufw deny 22/udp\ngufw deny 22/tcp\ngufw deny 22/udp\n");}
		System.out.print(BGRN+"Are you using mysql? [y/n]: ");
		System.out.print(RST);
		chr=scan.next().toLowerCase().charAt(0);
		if(chr=='y'){shl.append("ufw allow 3306/tcp\nufw allow 3306/udp\ngufw allow 3306/tcp\ngufw allow 3306/udp\n");}
		else{shl.append("ufw deny 3306/tcp\nufw deny 3306/udp\ngufw deny 3306/tcp\ngufw deny 3306/udp\n");}
		System.out.print(BGRN+"Are you using ftp? [y/n]: ");
		System.out.print(RST);
		chr=scan.next().toLowerCase().charAt(0);
		if(chr=='y'){shl.append("ufw allow 21/tcp\nufw allow 21/udp\ngufw allow 21/tcp\ngufw 21/udp\n");}
		else{shl.append("ufw deny 21/tcp\nufw deny 21/udp\ngufw deny 21/tcp\ngufw deny 21/udp\n");}
		System.out.print(BRED+"How many ports do you want to block?(0 if none(refer to the list)): "+RST);
		int count=scan.nextInt();
		int x=0;
		for(int i=0;i<count;i++){
			System.out.print("What port would you like to block: ");
			x=scan.nextInt();
			shl.append("ufw deny "+x+"/tcp\nufw deny "+x+"/udp\ngufw deny "+x+"/tcp\ngufw deny "+x"/udp\n");
		}
		System.out.print(RST);
		shl.append("ufw reset\nufw enable\nufw status\ngufw reset\ngufw enable\n");
		//useradd
		System.out.print(BBLU+"How many users should be added?(0 if none): "+RST);
		count=scan.nextInt();
		String name="";
		for(int i=0;i<count;i++){
			System.out.print(BRED+"Username: "+RST);
			name=scan.next();
			shl.append("useradd "+name+"\n");
			shl.append("echo -e \"Password1!\\nPassword1!\" | passwd "+name+"\npasswd --expire "+name+"\n");
			System.out.println(GRN+"User added with password: Password1!"+RST);
		}
		//userdel
		System.out.print(BBLU+"How many users should be deleted?(0 if none): "+RST);
		count=scan.nextInt();
		for(int i=0;i<count;i++){
			System.out.print(BRED+"Username: "+RST);
			name=scan.next();
			shl.append("userdel "+name+"\n");
			System.out.println(GRN+"User removed"+RST);
		}
		//groupadd
		System.out.print(BGRN+"Will you need to create a group? [y/n]"+RST);
		chr=scan.next().toLowerCase().charAt(0);
		if(chr=='y'){
			System.out.print(BRED+"Groupname: "+RST);
			String gname=scan.next();
			shl.append("groupadd "+gname+"\n");
			System.out.print(BRED+"How many users are you adding to this group (0 if none): "+RST);
			count=scan.nextInt();
			for(int i=0;i<count;i++){
				System.out.print(BGRN+"Username: "+RST);
				name=scan.next();
				shl.append("usermod -g "+gname+" "+name+"\n");
				System.out.println(BGRN+"User "+name+" added to group "+gname+RST);
			}
		}
		//password changes
		System.out.print(RED+"open a new terminal window and type \"sudo nano ./Downloads/cpp/users.txt\" type in the names of all the users(excluding yourself, and make sure you type the names exactly and each on a new line) in the system type anything and press enter to continue "+RST);
		name=scan.next();
		shl.append(shell.passwd("users.txt"));
		shell.printw(new StringBuilder(""),"users.txt");
		//admin to standard
		System.out.print(RED+">>>>NEXT<<<< re-open the \"users.txt\" file and this time, type the names of all the authorized users allowed on the system (excluding yourself and other admins, and make sure you type the names exactly and each on a new line) type anything and press enter to continue: "+RST);
		name=scan.next();
		shl.append(shell.perms("users.txt"));
		shell.printw(new StringBuilder(""),"users.txt");
		//password policies
		shl.append("rm /etc/login.defs\ncp "+PWD+"common-password /etc/\napt install libpam-pwquality -y\nrm /etc/pam.d/common-password\ncp "+PWD+"common-password /etc/pam.d/\n");
		//uninstall bad programs
		shl.append("apt-get purge kissmet -y\n");
		shl.append("prelink -ua\napt-get purge --autoremove prelink\n");
		shl.append("apt-get autoremove --purge zenmap\napt-get remove --auto-remove nmap\n");
		shl.append("systemctl disable xinetd\napt-get purge --autoremove openbsd-inetd inetutils-inetd\n");
		//critical services
		System.out.println(BRED+"What is the critical service? (none if none): "+RST);
		String cname=scan.next().toLowerCase();
		if(cname.contains("proftpd")){
			shl.append("apt-get install proftpd\ncat /etc/proftpd/proftpd.conf\nsystemctl restart proftpd\nsystemctl enable proftpd\n");
		}else if(cname.contains("vsftpd")){
			shl.append("apt-get install vsftpd\nsystemctl restart vsftpd\nsystemctl enable vsftpd\n");
		}else if(cname.contains("samba")){
			shl.append("apt-get install samba smbclient\nsystemctl restart smbd nmbd\nsystemctl enable smbd nmbd\n");
		}else if(cname.contains("ssh")){
			shl.append("apt-get install openssh-server\nsystemctl restart ssh\nsystemctl enable ssh\n");
		}else if(cname.contains("apache")){
			shl.append("apt-get install apache2\napt-get install libapache2-mod-security2\ngroupadd apache2\nuseradd -d /var/www/ -g apache2 -s /bin/nologin apache2\nsystemctl restart apache2\nsystemctl enable apache2\n");
		}else if(cname.contains("mysql")){
			shl.append("apt-get install mysql-server\nmysql -u root -p\nsystemctl restart mysql\nsystemctl enable mysql\n");
		}
		shl.append(shell.critserv(cname));
		shl.append("apt-get autoclean\n");
		//auditing
		shl.append("apt-get install auditd -y\nauditctl -e 1\nsystemctl restart auditd\nsystemctl enable auditd\n");
		//apparmor
		shl.append("apt-get install apparmor apparmor-utils\naa-enforce /etc/apparmor.d/*\nsystemctl stop autofs\nsystemctl disable autofs\n");
		//GRUB
		shl.append("grub-mkpasswd-pbkdf2\nupdate-grub\n");
		//antivirus and antirootkits
		shl.append("apt-get install clamav rkhunter\nfreshclam\nrkhunter --check\n");
		shell.printw(shl,"run.txt");
	}
}
