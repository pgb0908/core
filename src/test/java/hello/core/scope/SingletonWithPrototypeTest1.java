package hello.core.scope;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Scope;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

public class SingletonWithPrototypeTest1 {
    @Test
    void prototypeFind() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(PrototypeBean.class);
        PrototypeBean pb1 = ac.getBean(PrototypeBean.class);
        pb1.addCount();
        Assertions.assertThat(pb1.getCount()).isEqualTo(1);

        PrototypeBean pb2 = ac.getBean(PrototypeBean.class);
        pb2.addCount();
        Assertions.assertThat(pb2.getCount()).isEqualTo(1);
    }

    @Test
    void singletonClientUsePrototype() {
        AnnotationConfigApplicationContext ac = new AnnotationConfigApplicationContext(ClientBean.class, PrototypeBean.class);
        ClientBean cb1 = ac.getBean(ClientBean.class);
        int count1 = cb1.logic();
        Assertions.assertThat(count1).isEqualTo(1);

        ClientBean cb2 = ac.getBean(ClientBean.class);
        int count2 = cb2.logic();
        Assertions.assertThat(count2).isEqualTo(1);

    }

    @Scope("singleton")
    static class ClientBean {
        // private final PrototypeBean prototypeBean;

        @Autowired
        private ObjectProvider<PrototypeBean> prototypeBeanProvider;

        public int logic() {
            PrototypeBean prototypeBean = prototypeBeanProvider.getObject();
            prototypeBean.addCount();
            return prototypeBean.getCount();
        }
    }

    @Scope("prototype")
    static class PrototypeBean {
        private int count = 0;

        public void addCount() {
            count++;
        }

        public int getCount() {
            return this.count;
        }

        @PostConstruct
        public void init() {
            System.out.println("PrototypeBean.init : " + this);
        }

        @PreDestroy
        public void destroy() {
            System.out.println("PrototypeBean.destroy");
        }
    }
}
