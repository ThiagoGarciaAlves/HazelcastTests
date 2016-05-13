import com.hazelcast.config.Config;
import com.hazelcast.config.JoinConfig;
import com.hazelcast.config.NetworkConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

/**
 * @author Thiago Alves
 */
public class Start {
    public static void main(String[] args) {

        System.out.println("HazelcastTests START");

        Config cfg = new Config();

        NetworkConfig network = cfg.getNetworkConfig();
        JoinConfig join = network.getJoin();
        join.getMulticastConfig().setEnabled(false);

        join.getTcpIpConfig().addMember("192.168.0.157");
        join.getTcpIpConfig().addMember("192.168.0.205");

        join.getTcpIpConfig().setRequiredMember(null).setEnabled(true);

        cfg.setProperty("hazelcast.initial.min.cluster.size", "2");

        cfg.getGroupConfig().setName("HazelcastTests");

        cfg.setProperty("hazelcast.health.monitoring.level", "OFF");

        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);

        for (int i = 0; i < 1000; i++) {

            Lock counterLock = instance.getLock("counter");
            counterLock.lock();

            try {

                Map<String,Integer> counters = instance.getMap("counters");

                Integer currentId = counters.get("counterId");

                if (currentId == null) {
                    currentId = 0;
                } else {
                    currentId++;
                }

                List<Integer> idsList = instance.getList("ids");

                idsList.add(currentId);
                counters.put("counterId", currentId);

                System.out.println("Added counter "+currentId);

            } finally {
                counterLock.unlock();
            }

        }

        System.out.println("INICIANDO VERIFICAÇÃO DE DUPLICIDADES");

        // Verificar se houve duplicidades
        List<Integer> idsList = instance.getList("ids");
        List<Integer> auxList = new ArrayList<Integer>();
        for (Integer integer : idsList) {
            if (auxList.contains(integer)) {
                System.out.println("ID Duplicado: " + integer);
            } else {
                auxList.add(integer);
            }
        }

        System.out.println("FIM DA VERIFICAÇÃO DE DUPLICIDADES");

    }
}
