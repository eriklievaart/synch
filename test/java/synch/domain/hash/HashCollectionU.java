package synch.domain.hash;

import java.util.List;

import org.junit.Test;

import com.eriklievaart.toolkit.lang.api.check.Check;
import com.eriklievaart.toolkit.lang.api.check.CheckCollection;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;

public class HashCollectionU {

	@Test
	public void filterOnPath() {
		List<FileHash> list = NewCollection.list();

		String pathMoon = "Anime/Full Moon Wo/Full moon wo sagashite - Episode 039 [A.F.K.].avi";
		String hashMoon = "950b28ec4d06db3a5556d096cbf94b099d6a7613";
		list.add(new FileHash(pathMoon, 178794496, hashMoon));

		String pathKanon = "Anime/Kanon/Kanon 2006/Kanon_(2006)_-_21_-_Ronde_Without_You_-_ronde_-_[a.f.k.](2c88bd3a).avi";
		String hashKanon = "bffab3c5832e5d8dc84697d0e2a3fead323f2471";
		list.add(new FileHash(pathKanon, 183497456, hashKanon));

		HashCollection hashes = new HashCollection(list);
		CheckCollection.isSize(hashes.getHashes(), 2);

		HashCollection filtered = hashes.filterOnPath("Anime/Kanon");
		CheckCollection.isSize(filtered.getHashes(), 1);
		String actual = filtered.getHashes().get(0).getRelativePath();
		Check.isEqual(actual, "Kanon 2006/Kanon_(2006)_-_21_-_Ronde_Without_You_-_ronde_-_[a.f.k.](2c88bd3a).avi");
	}

	@Test
	public void similarPath() {
		List<FileHash> list = NewCollection.list();

		String pathMoon = "Anime/Full Moon Wo/Full moon wo sagashite - Episode 039 [A.F.K.].avi";
		String hashMoon = "950b28ec4d06db3a5556d096cbf94b099d6a7613";
		list.add(new FileHash(pathMoon, 178794496, hashMoon));

		String pathKanon = "Anime/Full/Kanon 2006/Kanon_(2006)_-_21_-_Ronde_Without_You_-_ronde_-_[a.f.k.](2c88bd3a).avi";
		String hashKanon = "bffab3c5832e5d8dc84697d0e2a3fead323f2471";
		list.add(new FileHash(pathKanon, 183497456, hashKanon));

		HashCollection hashes = new HashCollection(list);
		CheckCollection.isSize(hashes.getHashes(), 2);

		HashCollection filtered = hashes.filterOnPath("Anime/Full");
		CheckCollection.isSize(filtered.getHashes(), 1);
		String actual = filtered.getHashes().get(0).getRelativePath();
		Check.isEqual(actual, "Kanon 2006/Kanon_(2006)_-_21_-_Ronde_Without_You_-_ronde_-_[a.f.k.](2c88bd3a).avi");
	}
}