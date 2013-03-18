/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package concurrency.sharingobject;

/**
 *
 * @author vijay
 */
public class ThreadLocalDemo extends Thread {

    private static int counter;

    public static ThreadLocalDemo getInstance() {
        return new ThreadLocalDemo();
    }
    public ThreadLocal counterRef = new ThreadLocal() {

        @Override
        protected Object initialValue() {
            return new Session(counter++);
        }
    };

    private class Session {
        int sessionId;
        @Override
        public String toString() {
            return "Session(" + sessionId + ")";
        }
        public Session(int id) {
            sessionId = id;
        }

        public int getSessionId() {
            return sessionId;
        }
        
    }

    public static void main(String args[]) {
        ThreadLocalDemo t1 = ThreadLocalDemo.getInstance();
        ThreadLocalDemo t2 = ThreadLocalDemo.getInstance();
        t1.start();
        t2.start();
    }

    public void run() {
        System.out.println(Thread.currentThread().getName()
                + " has Session Object -> " + counterRef.get());
        new BusinessLogic1().execute();
        new BusinessLogic2().execute();
        new BusinessLogic3().execute();
    }
    
    class BusinessLogic1{
        void execute(){
            int newValue;
            //Execute Business Logic            
            System.out.println("Business Logic 1:"+counterRef.get());
            Session s = (Session)counterRef.get();
            if(s.getSessionId()==1){
                counterRef.set(new Session(5));
            }else{
                counterRef.set(new Session(6));
            }            
        }
    }
    
    class BusinessLogic2{
        void execute(){
            //Execute Business Logic            
            System.out.println("Business Logic 2:"+counterRef.get());
            
        }
    }
    
    class BusinessLogic3{
        void execute(){
            //Execute Business Logic            
            System.out.println("Business Logic 3:"+counterRef.get());
            
        }
    }
}
