import {RiTimeLine} from 'react-icons/ri'
import {BsHddNetwork} from 'react-icons/bs'
import moment from 'moment'

export default function NetWorkInterfaces({stat}) {
    let prev_stat = {
        measure: ['fetching details'],
        timestamp: Date.now()
    }
    prev_stat = stat ? stat : prev_stat
    return (
        <div className='infra-name-ctn' id='cpu'>
            <h4>Available Network Interfaces</h4>
            {prev_stat.measure.map((stat, index) => <div className='measure_stat' key={index}><BsHddNetwork /><p>{stat}</p></div>)}
            <div className='measure_stat' id='timestamp'>
                <RiTimeLine />
                <p>updated {moment(prev_stat.timestamp).fromNow()}</p>
            </div>
        </div>
    )
}