package io.realm;


import org.mockito.internal.util.reflection.Whitebox;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.List;

import static org.mockito.Matchers.anyInt;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

public class MockRealm {

    public static Realm mockRealm() {
        final Realm realm = mock(Realm.class);
        realm.handlerController = new HandlerController(realm);
        return realm;
    }

    public static <T extends RealmObject> RealmResults<T> mockRealmResult(Realm realm, final List<T> data) {
        final RealmResults results = mock(RealmResults.class);
        when(results.get(anyInt())).then(new Answer<T>() {
            @Override
            public T answer(InvocationOnMock invocation) throws Throwable {
                final int arg = (int) invocation.getArguments()[0];
                return data.get(arg);
            }
        });
        //results.realm = realm;
        Whitebox.setInternalState(results, "realm", realm);
        //noinspection unchecked
        return results;
    }
}