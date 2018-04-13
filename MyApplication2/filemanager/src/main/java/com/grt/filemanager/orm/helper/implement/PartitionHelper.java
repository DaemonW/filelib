package com.grt.filemanager.orm.helper.implement;

import com.grt.filemanager.orm.dao.Partition;
import com.grt.filemanager.orm.dao.base.PartitionDao;
import com.grt.filemanager.orm.helper.BaseHelper;

import java.util.List;

import de.greenrobot.dao.AbstractDao;

public class PartitionHelper extends BaseHelper<Partition, Long> {
    public PartitionHelper(AbstractDao dao) {
        super(dao);
    }

    public List<Partition> list() {
        return queryBuilder().list();
    }

    public Partition query(int id) {
        return queryBuilder().where(PartitionDao.Properties.Id.eq(id)).unique();
    }

    public void delete(int id) {
        queryBuilder().where(PartitionDao.Properties.Id.eq(id))
                .buildDelete().executeDeleteWithoutDetachingEntities();
    }

    public Partition updateTask(int id, int task) {
        Partition partition = queryBuilder().where(PartitionDao.Properties.Id.eq(id)).unique();
        if (partition != null) {
            partition.setTask(task);
            update(partition);
            return partition;
        } else {
            return null;
        }
    }

}
