import {RiTimeLine} from 'react-icons/ri'
import moment from 'moment'

export default function DiskUsage({stat}) {
    let prev_stat = {
        measure: 'fetching details',
        timestamp: Date.now()
    }
    prev_stat = stat ? stat : prev_stat
    const keys = prev_stat.measure.length === 2 ? prev_stat.measure[0].replace(/\s\s+/g, ' ').split(" ") : []
    const values = prev_stat.measure.length === 2 ? prev_stat.measure[1].replace(/\s\s+/g, ' ').split(" ") : []
    return (
        <div className='infra-name-ctn' id='disk'>
            <h4>Disk Usages</h4>
            {values.map((value, index) => (
                <div className="measure_stat" key={index}>
                    <p><b>{keys[index]}</b> : {value}</p>
                </div>)
            )}
            <div className='measure_stat' id='timestamp'>
                <RiTimeLine />
                <p>updated {moment(prev_stat.timestamp).fromNow()}</p>
            </div>
        </div>
    )
}