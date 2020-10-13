package io.coti.financialserver.http.data;

import io.coti.basenode.data.Hash;
import io.coti.financialserver.data.*;
import io.coti.financialserver.data.interfaces.IDisputeEvent;
import io.coti.financialserver.http.data.interfaces.IDisputeEventResponseData;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public enum DisputeEventResponseDataClass {
    DISPUTE(DisputeData.class, GetDisputeResponseData.class, FinancialServerEvent.NewDispute) {
        @Override
        public IDisputeEventResponseData getEventObject(DisputeEventData disputeEventData, Hash userHash, ActionSide evenDisplaySide) {
            return GetDisputeResponseClass.getByActionSide(evenDisplaySide).getNewInstance((DisputeData) disputeEventData.getEventObject(), userHash);
        }
    },
    DISPUTE_COMMENT(DisputeCommentData.class, GetCommentResponseData.class, FinancialServerEvent.NewDisputeComment),
    DISPUTE_DOCUMENT(DisputeDocumentData.class, DocumentNameResponseData.class, FinancialServerEvent.NewDisputeDocument),
    DISPUTE_STATUS_CHANGE_EVENT(DisputeStatusChangeEventData.class, DisputeStatusChangeResponseData.class, FinancialServerEvent.DisputeStatusUpdated),
    DISPUTE_ITEM_STATUS_CHANGE_EVENT(DisputeItemStatusChangeEventData.class, DisputeItemStatusChangeResponseData.class, FinancialServerEvent.DisputeItemStatusUpdated),
    DISPUTE_ITEM_VOTE(DisputeItemVoteData.class, DisputeItemVoteResponseData.class, FinancialServerEvent.NewDisputeItemVote);


    private Class<? extends IDisputeEvent> disputeEventClass;
    private Class<? extends IDisputeEventResponseData> disputeEventResponseClass;
    private FinancialServerEvent financialServerEvent;

    private static class DisputeEventResponseDataClasses {
        private static final Map<Class<? extends IDisputeEvent>, DisputeEventResponseDataClass> disputeEventResponseDataClassMap = new HashMap<>();
    }

    <T extends IDisputeEvent, S extends IDisputeEventResponseData> DisputeEventResponseDataClass(Class<T> disputeEventClass, Class<S> disputeEventResponseClass, FinancialServerEvent financialServerEvent) {
        this.disputeEventClass = disputeEventClass;
        DisputeEventResponseDataClasses.disputeEventResponseDataClassMap.put(disputeEventClass, this);
        this.disputeEventResponseClass = disputeEventResponseClass;
        this.financialServerEvent = financialServerEvent;
    }

    public static DisputeEventResponseDataClass getByDisputeEventClass(Class<? extends IDisputeEvent> disputeEventClass) {
        return DisputeEventResponseDataClasses.disputeEventResponseDataClassMap.get(disputeEventClass);
    }

    public IDisputeEventResponseData getEventObject(DisputeEventData disputeEventData, Hash userHash, ActionSide evenDisplaySide) {
        try {
            Constructor<? extends IDisputeEventResponseData> constructor = disputeEventResponseClass.getConstructor(disputeEventClass);
            return constructor.newInstance(disputeEventClass.cast(disputeEventData.getEventObject()));
        } catch (Exception e) {
            log.error("Error at getting event object", e);
            return null;
        }
    }

    public FinancialServerEvent getFinancialServerEvent() {
        return financialServerEvent;
    }
}