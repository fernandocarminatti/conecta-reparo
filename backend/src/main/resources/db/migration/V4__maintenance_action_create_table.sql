CREATE TABLE maintenance_action (
    id SERIAL PRIMARY KEY,
    public_id UUID NOT NULL UNIQUE,
    maintenance_id INT NOT NULL,
    executed_by VARCHAR(255),
    start_date TIMESTAMP WITH TIME ZONE,
    completion_date TIMESTAMP WITH TIME ZONE NOT NULL,
    action_description TEXT NOT NULL,
    outcome_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_action_maintenance
        FOREIGN KEY (maintenance_id)
        REFERENCES maintenance(id)
);
CREATE INDEX idx_action_maintenance_id ON maintenance_action(maintenance_id);

CREATE TABLE action_material (
    id SERIAL PRIMARY KEY,
    public_id UUID NOT NULL UNIQUE,
    maintenance_action_id SERIAL NOT NULL,
    item_name VARCHAR(255) NOT NULL,
    quantity NUMERIC(10, 2) NOT NULL,
    unit_of_measure VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,

    CONSTRAINT fk_material_action
        FOREIGN KEY (maintenance_action_id)
        REFERENCES maintenance_action(id)
);

CREATE INDEX idx_material_action_id ON action_material(maintenance_action_id);