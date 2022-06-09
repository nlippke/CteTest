/*
 * Copyright 2014 - 2022 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.example.sample;

import com.blazebit.persistence.CriteriaBuilder;
import java.util.List;
import org.example.model.Cat;
import org.example.model.CatCte;
import org.example.model.Person;
import org.hibernate.Filter;
import org.hibernate.Session;
import org.junit.Assert;
import org.junit.Test;

public class SampleTest extends AbstractSampleTest {

    @Test
    public void cteTestWithFilter() {
        transactional(em -> {

            Session hibernateSession = em.unwrap(Session.class);
            Filter filter = hibernateSession.enableFilter("genderFilter");
            filter.setParameter("gender", "f");

            CriteriaBuilder<Cat> cb = cbf.create(em, Cat.class);
            cb.from(Cat.class, "cat");
            cb.where("size(kittens)").ge(0);
            List<Cat> parents = cb.getResultList();

            System.out.println(parents);

            List<Cat> cats = cbf.create(em, Cat.class)
                    .where("id").in()
                    .fromSubquery(CatCte.class, "cte")
                    .from(Cat.class)
                    .bind("id").select("id")
                    .bind("rank").select("dense_rank() over (partition by mother order by id asc)")
                    .where("mother").in(parents)
                    .end()
                    .select("cte.id")
                    .where("cte.rank").ge(1).end()
                    .getResultList();

            System.out.println(cats);

            Assert.assertEquals(2, cats.size());
        });
    }
}
