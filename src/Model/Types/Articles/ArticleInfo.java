package Model.Types.Articles;

import java.io.Serializable;
import java.util.UUID;

public record ArticleInfo(UUID sellerID, UUID articleID, String display) implements Serializable {}
