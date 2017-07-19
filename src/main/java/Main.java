import com.m12u.amqp.AMQP;

/**
 *
 */
public class Main
{
    public static void main(String[] args) throws Exception {
        AMQP dialog = new AMQP();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
