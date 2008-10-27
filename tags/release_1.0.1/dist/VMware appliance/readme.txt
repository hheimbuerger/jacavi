JaCaVi Development VM:

    OS: JeOS 8.04.1

    Installierte Applikationen:
        - Subversion 1.4.6
        - Apache 2.2.8
        - Trac 0.11 final mit den Plugins:
            - TracAccountManager 0.2dev-r3554
            - TracCommitBackreferencer 0.1 (unveroeffentlichte interne Entwicklung)
        - OpenSSH 4.7p1
        - MySQL 5.0.51a3-ubuntu5.1
        - Samba 3.0.28a (wegen smbd um gegenueber Windows Clients auf Netbios-Namensaufloesungsanfragen reagieren zu koennen)

    User: user / jacavi (root ueber sudo)
    Datenbank-User: root / jacavi
                    trac / aeYeithe0Oph
    SVN-User: user / jacavi
    Trac-User: user / jacavi (hat TRAC_ADMIN-Rechte)

    Trac-URL: http://jacavi-dev/
    SVN-URL: svn://jacavi-dev/jacavi/trunk/

    Unterschiede zu der Version unter http://www.jacavi.de/:
        - SMTP wurde deaktiviert.
        - SVN-User wurden entfernt und durch 'user' ersetzt.
        - Read-only anonymous-Zugriff auf SVN ist moeglich.
        - Trac-User wurden gesperrt und 'user' ergaenzt.
        - Die angepassten SVN-Hooks wurden entfernt.
        - Snapshot vom 2008-10-24

    Verwendungsschritte:
        1. VMware Server 1.0.7 oder kompatible VMware-Software installieren.
        2. VM in den Server laden (File -> Open)
        3. VM starten. (Login oder ähnliches ist nicht notwendig.)
        4. Die VM sollte sich vom lokalen DHCP-Server eine IP-Adresse besorgen
           (Netzwerk ist auf 'Bridged' configuriert) und dann als "jacavi-dev"
           im Netzwerk erreichbar sein.

    Troubleshooting:
        - Gelegentlich bekommt die VM keine IP vom DHCP-Server, ein "sudo /etc/init.d/networking restart" hilft dann meist)
