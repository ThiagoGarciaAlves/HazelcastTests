import com.hazelcast.config.Config;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;

/**
 * @author Thiago Alves
 */
public class Start {
    public static void main(String[] args) {
        System.out.println("HazelcastTests START");
        Config cfg = new Config();
        HazelcastInstance instance = Hazelcast.newHazelcastInstance(cfg);
    }
}
