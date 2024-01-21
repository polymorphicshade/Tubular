package org.schabi.newpipe.local.sponsorblock;

import android.content.Context;

import org.schabi.newpipe.NewPipeDatabase;
import org.schabi.newpipe.database.AppDatabase;
import org.schabi.newpipe.database.sponsorblock.dao.SponsorBlockWhitelistDAO;
import org.schabi.newpipe.database.sponsorblock.dao.SponsorBlockWhitelistEntry;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SponsorBlockDataManager {
    private final SponsorBlockWhitelistDAO sponsorBlockWhitelistTable;

    public SponsorBlockDataManager(final Context context) {
        final AppDatabase database = NewPipeDatabase.getInstance(context);
        sponsorBlockWhitelistTable = database.sponsorBlockWhitelistDAO();
    }

    public Maybe<Long> addToWhitelist(final String uploader) {
        return Maybe.fromCallable(() -> {
            final SponsorBlockWhitelistEntry entry = new SponsorBlockWhitelistEntry(uploader);
            return sponsorBlockWhitelistTable.insert(entry);
        }).subscribeOn(Schedulers.io());
    }

    public Completable removeFromWhitelist(final String uploader) {
        return Completable.fromAction(() -> sponsorBlockWhitelistTable.deleteByUploader(uploader));
    }

    public Single<Boolean> isWhiteListed(final String uploader) {
        return Single.fromCallable(() -> sponsorBlockWhitelistTable.exists(uploader))
                .subscribeOn(Schedulers.io());
    }

    public Completable clearWhitelist() {
        return Completable.fromAction(sponsorBlockWhitelistTable::deleteAll);
    }
}
