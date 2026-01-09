CREATE TABLE pledge (
    id SERIAL PRIMARY KEY,
    public_id UUID NOT NULL UNIQUE,
    maintenance_id SERIAL NOT NULL,
    volunteer_name VARCHAR(255) NOT NULL,
    volunteer_contact VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    type VARCHAR(100) NOT NULL,
    status VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_pledge_maintenance
        FOREIGN KEY(maintenance_id)
            REFERENCES maintenance(id)
);

CREATE INDEX idx_pledge_maintenance_id ON pledge(maintenance_id);