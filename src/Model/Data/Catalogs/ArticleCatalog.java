package Model.Data.Catalogs;

import Model.Data.Exceptions.AlreadyExistsInCatalogException;
import Model.Data.Exceptions.NotFoundInCatalogException;
import Model.Types.Articles.Article;
import Model.Types.Articles.ArticleInfo;
import Model.Types.Carriers.Carrier;
import Model.Types.Exceptions.ArticleNegativeNumberException;
import Model.Types.Residence;
import Model.Types.User;

import java.io.Serializable;
import java.util.*;

public class ArticleCatalog extends GenericCatalog<Article> {
    private Map<String, ArrayList<Article>> usedDescriptions;
    public ArticleCatalog() {
        super();
        this.usedDescriptions = new HashMap<>();
    }
    public ArticleCatalog (Map<UUID, Article> articles) {
        super(articles);
        this.usedDescriptions = new HashMap<>();
    }
    public ArticleCatalog (ArticleCatalog articleCatalog) {
        this.catalog = articleCatalog.getCatalog();
        this.usedDescriptions = articleCatalog.getUsedDescriptions();
    }

    public Map<String, ArrayList<Article>> getUsedDescriptions() {
        return usedDescriptions;
    }

    @Override
    public String toString() {
        return "ArticleCatalog {\n" +
                " articles = " + this.catalog +
                "\n}";
    }

    public void add (Article article)  {
        super.add(article);
        if (this.usedDescriptions.containsKey(article.getDescription())) {
            this.usedDescriptions.get(article.getDescription()).add(article);
        } else {
            ArrayList<Article> newArticlesList = new ArrayList<>();
            newArticlesList.add(article);
            this.usedDescriptions.put(article.getDescription(), newArticlesList);
        }
    }

    public UUID get (String description) throws NotFoundInCatalogException {
        if (this.usedDescriptions.containsKey(description)) {
            ArrayList<Article> list = this.usedDescriptions.get(description);
            return list.get(0).getId();
        } else {
            throw new NotFoundInCatalogException();
        }
    }

    // to update prices
    public void notifyArticles (int currentYear) throws ArticleNegativeNumberException {
        for (Map.Entry<UUID, Article> entry : this.catalog.entrySet()) {
            Article article = entry.getValue();
            article.updatePremiumPriceCorrection(currentYear);
        }
    }
}
