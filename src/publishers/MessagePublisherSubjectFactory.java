/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package publishers;


public class MessagePublisherSubjectFactory {
    private synchronized static
        MessagePublisherSubject createMessagePublisherSubjectImpl() {
            return new MessagePublisherSubjectImpl();
    }

    protected synchronized static
        CurrentMarketPublisher createCurrentMarketPublisher() {
            return new CurrentMarketPublisher(createMessagePublisherSubjectImpl());
    }

    protected synchronized static
        LastSalePublisher createLastSalePublisher() {
                return new LastSalePublisher(createMessagePublisherSubjectImpl());
    }

    protected synchronized static
        TickerPublisher createTickerPublisher() {
            return new TickerPublisher(createMessagePublisherSubjectImpl());
    }

    protected synchronized static
        MessagePublisher createMessagePublisher() {
            return new MessagePublisher(createMessagePublisherSubjectImpl());
    }
}
