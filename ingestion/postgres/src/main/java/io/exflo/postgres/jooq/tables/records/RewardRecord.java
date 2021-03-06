/*
 * This file is generated by jOOQ.
 */
package io.exflo.postgres.jooq.tables.records;


import io.exflo.postgres.jooq.enums.DeltaType;
import io.exflo.postgres.jooq.tables.Reward;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.annotation.processing.Generated;

import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Row7;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.12.3"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class RewardRecord extends TableRecordImpl<RewardRecord> implements Record7<Long, DeltaType, String, BigDecimal, Long, String, Timestamp> {

    private static final long serialVersionUID = 2031512453;

    /**
     * Setter for <code>public.reward.id</code>.
     */
    public RewardRecord setId(Long value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>public.reward.id</code>.
     */
    public Long getId() {
        return (Long) get(0);
    }

    /**
     * Setter for <code>public.reward.delta_type</code>.
     */
    public RewardRecord setDeltaType(DeltaType value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>public.reward.delta_type</code>.
     */
    public DeltaType getDeltaType() {
        return (DeltaType) get(1);
    }

    /**
     * Setter for <code>public.reward.to</code>.
     */
    public RewardRecord setTo(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>public.reward.to</code>.
     */
    public String getTo() {
        return (String) get(2);
    }

    /**
     * Setter for <code>public.reward.amount</code>.
     */
    public RewardRecord setAmount(BigDecimal value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>public.reward.amount</code>.
     */
    public BigDecimal getAmount() {
        return (BigDecimal) get(3);
    }

    /**
     * Setter for <code>public.reward.block_number</code>.
     */
    public RewardRecord setBlockNumber(Long value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>public.reward.block_number</code>.
     */
    public Long getBlockNumber() {
        return (Long) get(4);
    }

    /**
     * Setter for <code>public.reward.block_hash</code>.
     */
    public RewardRecord setBlockHash(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>public.reward.block_hash</code>.
     */
    public String getBlockHash() {
        return (String) get(5);
    }

    /**
     * Setter for <code>public.reward.block_timestamp</code>.
     */
    public RewardRecord setBlockTimestamp(Timestamp value) {
        set(6, value);
        return this;
    }

    /**
     * Getter for <code>public.reward.block_timestamp</code>.
     */
    public Timestamp getBlockTimestamp() {
        return (Timestamp) get(6);
    }

    // -------------------------------------------------------------------------
    // Record7 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row7<Long, DeltaType, String, BigDecimal, Long, String, Timestamp> fieldsRow() {
        return (Row7) super.fieldsRow();
    }

    @Override
    public Row7<Long, DeltaType, String, BigDecimal, Long, String, Timestamp> valuesRow() {
        return (Row7) super.valuesRow();
    }

    @Override
    public Field<Long> field1() {
        return Reward.REWARD.ID;
    }

    @Override
    public Field<DeltaType> field2() {
        return Reward.REWARD.DELTA_TYPE;
    }

    @Override
    public Field<String> field3() {
        return Reward.REWARD.TO;
    }

    @Override
    public Field<BigDecimal> field4() {
        return Reward.REWARD.AMOUNT;
    }

    @Override
    public Field<Long> field5() {
        return Reward.REWARD.BLOCK_NUMBER;
    }

    @Override
    public Field<String> field6() {
        return Reward.REWARD.BLOCK_HASH;
    }

    @Override
    public Field<Timestamp> field7() {
        return Reward.REWARD.BLOCK_TIMESTAMP;
    }

    @Override
    public Long component1() {
        return getId();
    }

    @Override
    public DeltaType component2() {
        return getDeltaType();
    }

    @Override
    public String component3() {
        return getTo();
    }

    @Override
    public BigDecimal component4() {
        return getAmount();
    }

    @Override
    public Long component5() {
        return getBlockNumber();
    }

    @Override
    public String component6() {
        return getBlockHash();
    }

    @Override
    public Timestamp component7() {
        return getBlockTimestamp();
    }

    @Override
    public Long value1() {
        return getId();
    }

    @Override
    public DeltaType value2() {
        return getDeltaType();
    }

    @Override
    public String value3() {
        return getTo();
    }

    @Override
    public BigDecimal value4() {
        return getAmount();
    }

    @Override
    public Long value5() {
        return getBlockNumber();
    }

    @Override
    public String value6() {
        return getBlockHash();
    }

    @Override
    public Timestamp value7() {
        return getBlockTimestamp();
    }

    @Override
    public RewardRecord value1(Long value) {
        setId(value);
        return this;
    }

    @Override
    public RewardRecord value2(DeltaType value) {
        setDeltaType(value);
        return this;
    }

    @Override
    public RewardRecord value3(String value) {
        setTo(value);
        return this;
    }

    @Override
    public RewardRecord value4(BigDecimal value) {
        setAmount(value);
        return this;
    }

    @Override
    public RewardRecord value5(Long value) {
        setBlockNumber(value);
        return this;
    }

    @Override
    public RewardRecord value6(String value) {
        setBlockHash(value);
        return this;
    }

    @Override
    public RewardRecord value7(Timestamp value) {
        setBlockTimestamp(value);
        return this;
    }

    @Override
    public RewardRecord values(Long value1, DeltaType value2, String value3, BigDecimal value4, Long value5, String value6, Timestamp value7) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached RewardRecord
     */
    public RewardRecord() {
        super(Reward.REWARD);
    }

    /**
     * Create a detached, initialised RewardRecord
     */
    public RewardRecord(Long id, DeltaType deltaType, String to, BigDecimal amount, Long blockNumber, String blockHash, Timestamp blockTimestamp) {
        super(Reward.REWARD);

        set(0, id);
        set(1, deltaType);
        set(2, to);
        set(3, amount);
        set(4, blockNumber);
        set(5, blockHash);
        set(6, blockTimestamp);
    }
}
