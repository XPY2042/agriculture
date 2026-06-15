package com.ruoyi.framework.config;

import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import com.ruoyi.common.utils.ip.IpUtils;

/**
 * �������ӡ���������ʵ�ַ�����ڳ��� A�����ն�ͬ���η��ʣ���
 */
@Component
public class LanAccessStartupRunner implements ApplicationRunner
{
    @Value("${server.port:8080}")
    private int serverPort;

    @Override
    public void run(ApplicationArguments args)
    {
        List<String> lanIps = IpUtils.getLanIpv4Addresses();
        System.out.println();
        System.out.println("---------- ���������ʣ���� API�� ----------");
        System.out.println("  ����: http://127.0.0.1:" + serverPort);
        if (lanIps.isEmpty())
        {
            System.out.println("  δ��⵽���þ����� IPv4���������������ǽ");
        }
        else
        {
            for (String ip : lanIps)
            {
                System.out.println("  ������: http://" + ip + ":" + serverPort);
            }
        }
        System.out.println("  ǰ�˿�����Ĭ�϶˿� 80�������豸����� http://<����IP>");
        System.out.println("  Windows ����ǽ: ���� tools/open-lan-firewall.ps1�������Ա��");
        System.out.println("------------------------------------------");
        System.out.println();
    }
}
