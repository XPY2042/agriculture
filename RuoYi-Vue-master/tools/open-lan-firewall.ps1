# Ϊ�ǻ�ũҵ���ϵͳ���ž��������ʶ˿ڣ�Windows ����ǽ��
# ���Թ���Ա��������: �Ҽ� PowerShell -> �Թ���Ա�������У�Ȼ��ִ��:
#   Set-ExecutionPolicy -Scope Process Bypass -Force
#   & ".\tools\open-lan-firewall.ps1"

$ErrorActionPreference = 'Stop'

$rules = @(
    @{ Name = 'AgriMonitor-Frontend-80';  Port = 80;  Desc = '�ǻ�ũҵǰ�˿����� (npm run dev)' },
    @{ Name = 'AgriMonitor-Backend-8080'; Port = 8080; Desc = '�ǻ�ũҵ Spring Boot ���' }
)

foreach ($r in $rules) {
    $existing = Get-NetFirewallRule -DisplayName $r.Name -ErrorAction SilentlyContinue
    if ($existing) {
        Write-Host "[����] �����Ѵ���: $($r.Name)" -ForegroundColor Yellow
        continue
    }
    New-NetFirewallRule -DisplayName $r.Name `
        -Description $r.Desc `
        -Direction Inbound `
        -Action Allow `
        -Protocol TCP `
        -LocalPort $r.Port `
        -Profile Private, Domain | Out-Null
    Write-Host "[������] $($r.Name) TCP $($r.Port)" -ForegroundColor Green
}

Write-Host ''
Write-Host '������������ʾ:' -ForegroundColor Cyan
Write-Host '  1. ����������豸����ͬһ WiFi/����'
Write-Host '  2. ������� RuoYiApplication + ǰ�� npm run dev'
Write-Host '  3. �������豸������򿪿���̨��ӡ�� http://<����IP> ��ַ'
Write-Host ''

$ips = Get-NetIPAddress -AddressFamily IPv4 |
    Where-Object { $_.IPAddress -notlike '127.*' -and $_.PrefixOrigin -ne 'WellKnown' } |
    Select-Object -ExpandProperty IPAddress -Unique

if ($ips) {
    Write-Host '�������� IPv4:' -ForegroundColor Cyan
    foreach ($ip in $ips) {
        Write-Host "  ǰ�� http://${ip}:80"
        Write-Host "  ��� http://${ip}:8080"
    }
} else {
    Write-Host 'δ��⵽������ IPv4�������������ӡ�' -ForegroundColor Yellow
}
